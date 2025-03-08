import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { DashboardService } from '../services/dashboard.service';
import { Tip } from '../models/app.models';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit, OnDestroy {

  // inject db service
  private dbSvc = inject(DashboardService)

  // tip details
  tips: Tip[] = [] // to hold tip objects
  totalTips: number = 0 // to hold total sum amount
  musicianId: string = '' // to be converted from long
  columns: string[] = ['id', 'amount', 'stripeChargeId'] // for display

  // sub
  protected dbSub!: Subscription

  ngOnInit(): void {
    this.loadTips()
  }

  // to retrieve tips by musician id
  loadTips() {
    this.dbSub = this.dbSvc.getTips(this.musicianId).subscribe(
      (tips: Tip[]) => {
        this.tips = tips
        this.totalTips = tips.reduce((sum, tip) => sum + tip.amount, 0) // sets an initial sum of 0 and adds each tip amount
      },
      error => console.error('>>> Error loading tips: ', error)
    )
  }

  // unsub
  ngOnDestroy(): void {
    if (this.dbSub) {
      this.dbSub.unsubscribe()
    }
  }

}
