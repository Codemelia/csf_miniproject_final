import { AfterViewInit, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Tip } from '../../models/app.models';
import { map, Observable, Subscription } from 'rxjs';
import { TipService } from '../../services/tip.service';
import { AuthStore } from '../../stores/auth.store';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';

@Component({
  selector: 'app-tips-history',
  standalone: false,
  templateUrl: './tips-history.component.html',
  styleUrl: './tips-history.component.scss'
})
export class TipsHistoryComponent implements OnInit, AfterViewInit, OnDestroy {

  // services
  private tipSvc = inject(TipService)
  private authStore = inject(AuthStore)

  // variables
  tips$!: Observable<Tip[]>
  totalTipAmount!: number
  totalTipCount: number = 0
  artisteId: string | null = null
  isViewInitialised: boolean = false

  // tip_id, tipper_name, tipper_message, amount, payment_intent_id, updated_at
  columns: string[] = [
    'tipId', 'tipperName', 'tipperMessage', 
    'netAmount', 'commission', 'paymentIntentId', 'updatedAt'
  ]

  // pagination
  dataSource = new MatTableDataSource<Tip>()

  @ViewChild(MatPaginator)
  paginator!: MatPaginator

  // sorting by date
  @ViewChild(MatSort)
  sort!: MatSort

  sortDirection: 'asc' | 'desc' | '' = 'desc'

  // sub
  private tipSub!: Subscription

  artisteExists: boolean = false

  ngOnInit(): void {

    // authentication
    this.artisteId = this.authStore.extractUIDFromToken()
    this.checkIfArtisteExists()

    this.fetchTips() // fetch tips on init
  }

  // checking if artiste exists
  checkIfArtisteExists(): void {
    const existString = localStorage.getItem("artisteExists")
    if (existString) this.artisteExists = JSON.parse(existString)
      else this.artisteExists = false
  }

  // fetch tips for artiste
  fetchTips(): void {
    console.log('>>> Fetching tips...')
    this.tips$ = this.tipSvc.getTips()

    this.tipSub = this.tips$.pipe(
      map(
        (tips: Tip[]) => {
          this.totalTipAmount = tips.reduce((sum, tip) => sum + tip.amount, 0) // get 
          this.totalTipCount = tips.reduce((qty, tip) => qty + 1, 0)
          this.dataSource.data = tips;
          this.dataSource.paginator?.firstPage() // reset paginator to first page
        }
      )
    ).subscribe()

  }

  // sorter for tip date
  toggleSort() {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc'

    if (this.sort) {
      this.sort.direction = this.sortDirection
      this.sort.active = 'updatedAt'
      this.dataSource.sort = this.sort
    }
  }

  ngAfterViewInit() {
    this.isViewInitialised = true
    this.dataSource.paginator = this.paginator
    this.dataSource.sort = this.sort

    this.sort.active = 'updatedAt'
    this.sort.direction = this.sortDirection
    this.dataSource.sort = this.sort
  }

  ngOnDestroy(): void {
    this.tipSub!.unsubscribe()
  }

}