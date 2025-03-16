import { Component, inject } from '@angular/core';
import { ArtisteService } from '../../services/artiste.service';

@Component({
  selector: 'app-overview',
  standalone: false,
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss'
})
export class OverviewComponent {

  summaryData: any = {};
  monthlyChart: any;
  dailyChart: any;

  private artisteSvc = inject(ArtisteService)

}
