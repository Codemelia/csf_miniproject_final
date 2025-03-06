import { Component, inject, OnInit } from '@angular/core';
import { TipService } from '../services/tip.service';

@Component({
  selector: 'app-tip-coupon',
  standalone: false,
  templateUrl: './tip-coupon.component.html',
  styleUrl: './tip-coupon.component.scss'
})
export class TipCouponComponent implements OnInit {

  private tipSvc = inject(TipService)

  amount$ = this.tipSvc.amount$;
  buskerId$ = this.tipSvc.buskerId$;

  ngOnInit() {
    this.tipSvc.setTip({ amount: 5, buskerId: 'busker1' });
  }
}