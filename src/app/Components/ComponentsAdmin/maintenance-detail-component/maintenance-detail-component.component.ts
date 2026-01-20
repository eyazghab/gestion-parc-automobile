import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';


@Component({
  selector: 'app-maintenance-detail-component',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './maintenance-detail-component.component.html',
  styleUrl: './maintenance-detail-component.component.css'
})
export class MaintenanceDetailComponentComponent implements OnInit {
   /*maintenance: Maintenance | null = null;
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private maintenanceService: MaintenanceService
  ) {}
 */
  ngOnInit(): void {
     /*
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.maintenanceService.getMaintenanceById(id).subscribe(data => {
      this.maintenance = data;
      this.loading = false;
    });
  }
}
 */}}