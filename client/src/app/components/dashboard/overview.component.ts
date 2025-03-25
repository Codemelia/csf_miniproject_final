import { AfterViewInit, Component, inject, OnDestroy, OnInit } from '@angular/core';

import { Chart, registerables } from 'chart.js';
import { Tip } from '../../models/app.models';
import { Observable } from 'rxjs';
import { TipService } from '../../services/tip.service';

// prepare chart
Chart.register(...registerables)

// data for summary cards
interface SummaryData {
  totalTipsEarned: number
  tipsThisMonth: number
  tipsCountThisMonth: number
  highestSingleTip: number
  averageTipAmount: number
}

@Component({
  selector: 'app-overview',
  standalone: false,
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss'
})
export class OverviewComponent implements OnInit, AfterViewInit, OnDestroy {

  // services
  private tipSvc = inject(TipService)

  // observables
  tips$!: Observable<Tip[]>
  summaryData: SummaryData = {
      totalTipsEarned: 0,
      tipsThisMonth: 0,
      tipsCountThisMonth: 0,
      highestSingleTip: 0,
      averageTipAmount: 0
  }

  // chart set up 
  private tips: Tip[] = []
  private monthlyChart: Chart | null = null
  private dailyChart: Chart | null = null
  isViewInitialized: boolean = false;

  ngOnInit(): void {
      this.tips$ = this.tipSvc.getTips() // get tips on init
      this.tips$.subscribe(tips => {
        this.tips = tips // set tips
        this.calculateSummaryData(tips) // calc summary cards data
        if (this.isViewInitialized) {
          this.setupCharts(tips); // update charts if view is initialized
        }
      });
  }

  ngAfterViewInit(): void {
    this.isViewInitialized = true // set true after view init
    this.setupCharts(this.tips) // set up charts after view init
  }

  // calculate data for summary cards
  private calculateSummaryData(tips: Tip[]): void {

    // total tips earned
    this.summaryData.totalTipsEarned = tips.reduce((sum, tip) => sum + tip.amount, 0)
    console.log('>>> Total tips earned: ', this.summaryData.totalTipsEarned)

    // monthly tips
    // get curr date details
    const now = new Date()
    const currentMonth = now.getMonth()
    const currentYear = now.getFullYear()

    // filter tips and get dates that match current month and year
    const thisMonthTips = tips.filter(tip => {
      const tipDate: Date = new Date(tip.updatedAt!) // date type safety
      if (!tipDate) return false // dont filter if date null
      return tipDate.getMonth() === currentMonth && tipDate.getFullYear() === currentYear // filter if date month and year matches curr month and year
    })

    // monthly tips
    this.summaryData.tipsThisMonth = thisMonthTips.reduce((sum, tip) => sum + tip.amount, 0)
    console.log('>>> Tips earned this month: ', this.summaryData.tipsThisMonth)

    // monthly tip count
    this.summaryData.tipsCountThisMonth = thisMonthTips.length
    console.log('>>> Tips count this month: ', this.summaryData.tipsCountThisMonth)

    // highest tip
    this.summaryData.highestSingleTip = tips.length > 0 ? Math.max(...tips.map(tip => tip.amount)) : 0
    console.log('>>> Highest tip this month: ', this.summaryData.highestSingleTip)

    // avg tip amt
    this.summaryData.averageTipAmount = tips.length > 0 ? this.summaryData.totalTipsEarned / tips.length : 0
    console.log('>>> Avg tip amount this month: ', this.summaryData.averageTipAmount)
  }

  // set up monthly and daily charts
  private setupCharts(tips: Tip[]): void {

    // destroy existing charts if they exist
    this.monthlyChart?.destroy()
    this.dailyChart?.destroy()

    // month
    const monthlyData = this.getMonthlyData(tips)
    this.monthlyChart = new Chart('monthlyChart', {
        type: 'line',
        data: {
            labels: monthlyData.labels,
            datasets: [{
                label: 'Tips Amount (SGD)',
                data: monthlyData.values,
                borderColor: '#9C27B0',
                backgroundColor: 'rgba(156, 39, 176, 0.2)',
                fill: true,
                tension: 0.3
            }]
        },
        options: {
          responsive: true,
          plugins: { legend: { display: false } }, // remove legend
          scales: {
              y: { beginAtZero: true, title: { display: true, text: 'Amount (SGD)' } },
              x: { title: { display: false, text: 'Month' } } // alr in title
          }
        }
    })

    // day
    const dailyData = this.getDailyData(tips);
    this.dailyChart = new Chart('dailyChart', {
        type: 'bar',
        data: {
            labels: dailyData.labels,
            datasets: [{
                label: 'Daily Tips (SGD)',
                data: dailyData.values,
                backgroundColor: '#9C27B0',
                borderColor: '#9C27B0',
                borderWidth: 1
            }]
        },
        options: {
          responsive: true,
          plugins: { legend: { display: false } }, // remove legend
          scales: {
              y: { beginAtZero: true, title: { display: true, text: 'Amount (SGD)' } },
              x: { title: { display: false, text: 'Day' } } // alr in title
          }
        }
    })
  }

  // get data for monthly chart
  private getMonthlyData(tips: Tip[]): { labels: string[], values: number[] } {
      const labels: string[] = []
      const values: number[] = []
      const now = new Date()
      const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

      for (let i = 11; i >= 0; i--) {
          const date = new Date(now.getFullYear(), now.getMonth() - i, 1)
          const month = months[date.getMonth()];
          const year = date.getFullYear()
          labels.push(`${month} ${year}`)

          // if tips are empty, push 0
          if (tips.length === 0) {
            values.push(0)
            continue
          }

          const monthlyTips = tips.filter(tip => {
            const tipDate = new Date(tip.updatedAt!) // date type safety
            return tipDate.getMonth() === date.getMonth() && tipDate.getFullYear() === date.getFullYear()
          });
          const total = monthlyTips.reduce((sum, tip) => sum + tip.amount, 0)
          values.push(total)
      }

      return { labels, values }
  }

  // get data for daily chart
  private getDailyData(tips: Tip[]): { labels: string[], values: number[] } {
      const labels: string[] = []
      const values: number[] = []
      const now = new Date()

      // Get the past 30 days
      for (let i = 29; i >= 0; i--) {
          const date = new Date(now)
          date.setDate(now.getDate() - i)
          const day = date.getDate()
          const month = date.toLocaleString('default', { month: 'short' })
          labels.push(`${day} ${month}`)

          // if tips are empty, push 0
          if (tips.length === 0) {
            values.push(0)
            continue
          }

          const dailyTips = tips.filter(tip => {
            const tipDate = new Date(tip.updatedAt!) // date type safety
              return tipDate.getDate() === date.getDate() &&
                tipDate.getMonth() === date.getMonth() &&
                tipDate.getFullYear() === date.getFullYear()
          })
          const total = dailyTips.reduce((sum, tip) => sum + tip.amount, 0)
          values.push(total)
      }

      return { labels, values }
  }

  // destroy charts
  ngOnDestroy(): void {
    this.monthlyChart?.destroy()
    this.dailyChart?.destroy()
  }

}
