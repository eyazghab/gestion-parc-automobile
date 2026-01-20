package com.example.demo.service.imp;

import com.example.demo.Repository.ArticleRepository;
import com.example.demo.Repository.CommandeRepository;
import com.example.demo.Repository.FournisseurRepository;
import com.example.demo.Repository.LigneComRepository;
import com.example.demo.model.*;
import com.example.demo.model.Commande.StatutCommande;
import com.example.demo.service.CommandeService;
import com.example.demo.service.EmailService;
import com.example.demo.service.QRCodeService;
import com.lowagie.text.*;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Service
@Transactional
public class CommandeServiceImp implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final FournisseurRepository fournisseurRepository;
    private final ArticleRepository articleRepository;
    private final EmailService emailService;
    private final LigneComRepository ligneCommandeRepository;
    private final QRCodeService qrCodeService;

    public CommandeServiceImp(CommandeRepository commandeRepository,
                              FournisseurRepository fournisseurRepository,
                              ArticleRepository articleRepository,
                              EmailService emailService,
                              LigneComRepository ligneCommandeRepository,
                              QRCodeService qrCodeService) {
        this.commandeRepository = commandeRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.articleRepository = articleRepository;
        this.emailService = emailService;
        this.ligneCommandeRepository=ligneCommandeRepository;
        this.qrCodeService=qrCodeService;
    }

    @Override
    public Commande creerCommande(Long fournisseurId, List<Long> articlesIds, String commentaire, int quantiteParArticle) {
        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));

        Commande commande = new Commande();
        commande.setFournisseur(fournisseur);
        commande.setDate(new Date());
        commande.setDate_creation(new Date());
        commande.setDate_modification(new Date());
        commande.setCommentaire(commentaire != null ? commentaire : "Commande créée automatiquement");
        commande.setStatut(StatutCommande.EN_ATTENTE);

        int lastNum = commandeRepository.findTopByOrderByNumeroCommandeDesc()
                .map(Commande::getNumeroCommande)
                .orElse(0);
        commande.setNumeroCommande(lastNum + 1);

        List<LigneCommande> lignes = new ArrayList<>();
        for (Long articleId : articlesIds) {
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new RuntimeException("Article non trouvé"));

            if (!article.getFournisseur().getIdFournisseur().equals(fournisseurId)) {
                throw new RuntimeException("L'article " + article.getNom() + " n'appartient pas à ce fournisseur");
            }

            LigneCommande ligne = new LigneCommande();
            ligne.setArticle(article);
            ligne.setQuantite(quantiteParArticle);
            ligne.setPrixUnitaire(article.getPrix());
            ligne.setCommande(commande);
            lignes.add(ligne);
        }
        commande.setLignes(lignes);

        double montantHT = lignes.stream().mapToDouble(l -> l.getPrixUnitaire() * l.getQuantite()).sum();
        double montantTVA = montantHT * 0.19;
        double montantTTC = montantHT + montantTVA;

        commande.setMontant_ht(montantHT);
        commande.setMontant_tva(montantTVA);
        commande.setMontant_ttc(montantTTC);

        Facture facture = new Facture();
        facture.setNumeroFacture("FAC-" + UUID.randomUUID());
        facture.setDateFacture(new Date());
        facture.setMontantHT(montantHT);
        facture.setMontantTVA(montantTVA);
        facture.setMontantTTC(montantTTC);
        facture.setEtat(Facture.EtatFacture.NON_PAYEE);
        facture.setCommande(commande);
        commande.setFacture(facture);

        // **Ne pas envoyer d'email ici**
        return commandeRepository.save(commande);
    }

    
@Override
public Commande updateCommande(Long commandeId, Long fournisseurId, List<Long> articleIds, List<Integer> quantites, String commentaire) {
    // Récupération de la commande existante
    Commande commande = commandeRepository.findById(commandeId)
            .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

    // Récupération du fournisseur
    Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
            .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));

    // Mise à jour des informations générales
    commande.setFournisseur(fournisseur);
    commande.setCommentaire(commentaire != null ? commentaire : "");
    commande.setDate_modification(new Date());

    // Fusion des quantités pour articles identiques
    Map<Long, Integer> ligneMap = new HashMap<>();
    for (int i = 0; i < articleIds.size(); i++) {
        Long artId = articleIds.get(i);
        Integer qte = quantites.get(i);
        ligneMap.put(artId, ligneMap.getOrDefault(artId, 0) + qte);
    }

    // Création des nouvelles lignes
    List<LigneCommande> nouvellesLignes = new ArrayList<>();
    for (Map.Entry<Long, Integer> entry : ligneMap.entrySet()) {
        Article article = articleRepository.findById(entry.getKey())
                .orElseThrow(() -> new RuntimeException("Article introuvable"));

        if (!article.getFournisseur().getIdFournisseur().equals(fournisseurId)) {
            throw new RuntimeException("L'article " + article.getNom() + " n'appartient pas à ce fournisseur");
        }

        LigneCommande ligne = new LigneCommande();
        ligne.setArticle(article);
        ligne.setQuantite(entry.getValue());
        ligne.setPrixUnitaire(article.getPrix());
        ligne.setCommande(commande); // relation bidirectionnelle
        nouvellesLignes.add(ligne);
    }

    // Supprimer les anciennes lignes en base (si orphanRemoval n'est pas activé)
    if (commande.getLignes() != null && !commande.getLignes().isEmpty()) {
        ligneCommandeRepository.deleteAll(commande.getLignes());
        commande.getLignes().clear();
    }

    // Ajouter les nouvelles lignes
    commande.getLignes().addAll(nouvellesLignes);

    // Recalcul des montants
    double montantHT = nouvellesLignes.stream().mapToDouble(l -> l.getQuantite() * l.getPrixUnitaire()).sum();
    double montantTVA = montantHT * 0.19;
    double montantTTC = montantHT + montantTVA;

    commande.setMontant_ht(montantHT);
    commande.setMontant_tva(montantTVA);
    commande.setMontant_ttc(montantTTC);

    return commandeRepository.save(commande);
}



    @Override
    public Commande passerEnCours(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID : " + commandeId));

        if (commande.getStatut() == StatutCommande.EN_COURS) {
            throw new RuntimeException("La commande est déjà en cours.");
        }

        commande.setStatut(StatutCommande.EN_COURS);
        Commande updatedCommande = commandeRepository.save(commande);

        // **Envoyer l'email uniquement ici**
        sendFactureEmail(updatedCommande);
        return updatedCommande;
    }

    // Nouvelle méthode delete
    @Override
    public void delete(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        commandeRepository.delete(commande);
    }

@Override
public Commande creerCommandeAvecQuantites(Long fournisseurId, List<Long> articleIds, List<Integer> quantites, String commentaire) {
    if (articleIds.size() != quantites.size()) {
        throw new RuntimeException("Le nombre d'articles et de quantités doit être identique");
    }

    Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
            .orElseThrow(() -> new RuntimeException("Fournisseur introuvable"));

    Commande commande = new Commande();
    commande.setFournisseur(fournisseur);
    commande.setCommentaire(commentaire != null ? commentaire : "Commande créée automatiquement");
    commande.setDate(new Date());
    commande.setStatut(StatutCommande.EN_ATTENTE);

    // Numéro auto-incrément
    int lastNum = commandeRepository.findTopByOrderByNumeroCommandeDesc()
            .map(Commande::getNumeroCommande)
            .orElse(0);
    commande.setNumeroCommande(lastNum + 1);

    // Fusion des lignes par articleId
    Map<Long, Integer> ligneMap = new HashMap<>();
    for (int i = 0; i < articleIds.size(); i++) {
        Long artId = articleIds.get(i);
        Integer qte = quantites.get(i);
        ligneMap.put(artId, ligneMap.getOrDefault(artId, 0) + qte);
    }

    List<LigneCommande> lignes = new ArrayList<>();
    for (Map.Entry<Long, Integer> entry : ligneMap.entrySet()) {
        Article article = articleRepository.findById(entry.getKey())
                .orElseThrow(() -> new RuntimeException("Article introuvable"));

        if (!article.getFournisseur().getIdFournisseur().equals(fournisseurId)) {
            throw new RuntimeException("L'article " + article.getNom() + " n'appartient pas à ce fournisseur");
        }

        LigneCommande ligne = new LigneCommande();
        ligne.setArticle(article);
        ligne.setQuantite(entry.getValue());
        ligne.setPrixUnitaire(article.getPrix());
        ligne.setCommande(commande);
        lignes.add(ligne);
    }
    commande.setLignes(lignes);

    // Calcul des montants
    double montantHT = lignes.stream().mapToDouble(l -> l.getQuantite() * l.getPrixUnitaire()).sum();
    double montantTVA = montantHT * 0.19;
    double montantTTC = montantHT + montantTVA;

    commande.setMontant_ht(montantHT);
    commande.setMontant_tva(montantTVA);
    commande.setMontant_ttc(montantTTC);

    // Création de la facture associée
    Facture facture = new Facture();
    facture.setNumeroFacture("FAC-" + UUID.randomUUID());
    facture.setDateFacture(new Date());
    facture.setMontantHT(montantHT);
    facture.setMontantTVA(montantTVA);
    facture.setMontantTTC(montantTTC);
    facture.setEtat(Facture.EtatFacture.NON_PAYEE);
    facture.setCommande(commande);
    commande.setFacture(facture);

    // Sauvegarde de la commande
    return commandeRepository.save(commande);
}



    @Override
    public Commande changerStatut(Long commandeId, StatutCommande statut) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        commande.setStatut(statut);
        commande.setDate_modification(new Date());
        return commandeRepository.save(commande);
    }

    @Override
    public List<Commande> getCommandes() {
        return commandeRepository.findAll();
    }

    @Override
    public Commande getCommandeById(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
    }

    @Override
public byte[] generateFacturePDFWithQRCode(Commande commande) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4, 36, 36, 36, 36);
    PdfWriter.getInstance(document, baos);
    document.open();

    // ===== 1️⃣ Titre =====
    Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
    titleFont.setColor(Color.BLACK);
    Paragraph title = new Paragraph("Facture Commande #" + commande.getNumeroCommande(), titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);
    document.add(Chunk.NEWLINE);

    // ===== 2️⃣ Infos fournisseur =====
    PdfPTable infoTable = new PdfPTable(2);
    infoTable.setWidthPercentage(100);
    infoTable.setSpacingBefore(10f);
    infoTable.setSpacingAfter(10f);
    infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

    Font infoFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
    infoFont.setColor(Color.BLACK);

    infoTable.addCell(new Phrase("Fournisseur : " + commande.getFournisseur().getNomFournisseur(), infoFont));
    infoTable.addCell(new Phrase("Date : " + new SimpleDateFormat("yyyy-MM-dd").format(commande.getDate()), infoFont));
    infoTable.addCell(new Phrase("Montant HT : " + commande.getMontant_ht(), infoFont));
    infoTable.addCell(new Phrase("Montant TVA : " + commande.getMontant_tva(), infoFont));
    infoTable.addCell(new Phrase("Montant TTC : " + commande.getMontant_ttc(), infoFont));
    infoTable.addCell(new Phrase("")); // cellule vide
    document.add(infoTable);
    document.add(Chunk.NEWLINE);

    // ===== 3️⃣ Tableau des articles =====
    PdfPTable table = new PdfPTable(5); // Nom, Réf, Qté, PU, Total
    table.setWidthPercentage(100);
    table.setSpacingBefore(10f);
    table.setSpacingAfter(10f);
    table.setWidths(new float[]{3, 2, 1, 2, 2});

    Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
    headerFont.setColor(Color.WHITE);
    Color headerColor = new Color(0, 121, 107); // vert foncé

    String[] headers = {"Nom Article", "Référence", "Quantité", "Prix Unitaire", "Total"};
    for (String h : headers) {
        PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
        cell.setBackgroundColor(headerColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    Font rowFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
    rowFont.setColor(Color.BLACK);

    for (LigneCommande ligne : commande.getLignes()) {
        PdfPCell cell1 = new PdfPCell(new Phrase(ligne.getArticle().getNom(), rowFont));
        PdfPCell cell2 = new PdfPCell(new Phrase(ligne.getArticle().getReference(), rowFont));
        PdfPCell cell3 = new PdfPCell(new Phrase(String.valueOf(ligne.getQuantite()), rowFont));
        PdfPCell cell4 = new PdfPCell(new Phrase(String.valueOf(ligne.getPrixUnitaire()), rowFont));
        PdfPCell cell5 = new PdfPCell(new Phrase(String.valueOf(ligne.getQuantite() * ligne.getPrixUnitaire()), rowFont));

        for (PdfPCell c : Arrays.asList(cell1, cell2, cell3, cell4, cell5)) {
            c.setPadding(5f);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        }

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
    }

    document.add(table);
    document.add(Chunk.NEWLINE);

    // ===== 4️⃣ QR Code =====
    byte[] qrBytes = qrCodeService.generateSignedQRCode(
            "FAC-" + commande.getNumeroCommande(),
            commande.getMontant_ttc(),
            new SimpleDateFormat("yyyy-MM-dd").format(commande.getDate())
    );
    Image qrImage = Image.getInstance(qrBytes);
    qrImage.setAlignment(Element.ALIGN_CENTER);
    qrImage.scaleToFit(150, 150);
    document.add(qrImage);

    document.close();
    return baos.toByteArray();
}



    
    @Override
public void sendFactureEmail(Commande commande) {
    try {
        // Générer le PDF avec QR Code
        byte[] pdf = generateFacturePDFWithQRCode(commande);

        // Envoyer l'email avec le PDF
        emailService.sendEmailWithAttachment(
                commande.getFournisseur().getContact(),
                "Facture Commande #" + commande.getNumeroCommande(),
                "Bonjour,\nVeuillez trouver ci-joint la facture de votre commande.",
                pdf,
                "Facture_Commande_" + commande.getNumeroCommande() + ".pdf"
        );
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Erreur lors de l'envoi de la facture par email : " + e.getMessage());
    }
}


    @Override
    public Commande findById(Long id) {
        return commandeRepository.findById(id).orElse(null);
    }

    @Override
    public Commande save(Commande commande) {
        return commandeRepository.save(commande);
    }
    
    @Override
public Commande livrerCommande(Long commandeId) {
    // 1️⃣ Récupérer la commande
    Commande commande = commandeRepository.findById(commandeId)
            .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

    // 2️⃣ Vérifier si elle peut être livrée
    if (commande.getStatut() != StatutCommande.EN_COURS) {
        throw new RuntimeException("La commande ne peut être livrée que si elle est en attente");
    }

    // 3️⃣ Mettre à jour le statut
    commande.setStatut(StatutCommande.LIVREE);
    commande.setDate_modification(new Date());

    // 4️⃣ Mettre à jour la facture si elle existe
    Facture facture = commande.getFacture();
    if (facture != null && facture.getEtat() == Facture.EtatFacture.NON_PAYEE) {
        facture.setEtat(Facture.EtatFacture.NON_PAYEE);
    }

    // 5️⃣ Sauvegarder la commande
    Commande savedCommande = commandeRepository.save(commande);

    // 6️⃣ Générer et envoyer le PDF de la facture par email
    try {
        sendFactureEmail(savedCommande);
    } catch (Exception e) {
        // Ne bloque pas la livraison si l'email échoue
        System.err.println("Erreur lors de l'envoi de l'email pour la commande #" 
                + commande.getNumeroCommande() + " : " + e.getMessage());
        e.printStackTrace();
    }

    return savedCommande;
}


   
   
}
