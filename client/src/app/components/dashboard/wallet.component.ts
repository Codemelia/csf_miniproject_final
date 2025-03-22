import { Component, inject, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ArtisteService } from '../../services/artiste.service';

interface Payout {
    date: Date;
    amount: number;
    status: 'Completed' | 'Pending' | 'Failed';
}

@Component({
    selector: 'app-wallet',
    standalone: false,
    templateUrl: './wallet.component.html',
    styleUrls: ['./wallet.component.scss']
})
export class WalletComponent /*implements OnInit*/ {
    walletBalance: number = 0
    pendingPayouts: number = 0
    payoutHistory: Payout[] = []
    payoutColumns: string[] = ['date', 'amount', 'status']
    dataSource = new MatTableDataSource<Payout>(this.payoutHistory)

    // snackbar
    private snackBar = inject(MatSnackBar)

    // services
    private artisteSvc = inject(ArtisteService)

    
    /*
    ngOnInit(): void {
      this.loadWalletData(); // load wallet data on init
    }

    // load wallet data
    private loadWalletData(): void {

      // get wallet balance
      this.artisteSvc.getWalletBalance().subscribe(balance => {
          this.walletBalance = balance
      })

      // get pending payouts
      this.artisteSvc.getPendingPayouts().subscribe(pending => {
          this.pendingPayouts = pending
      })

      // get payout history
      this.artisteSvc.getPayoutHistory().subscribe(history => {
          this.payoutHistory = history
          this.dataSource.data = this.payoutHistory
      })
    }
    */

    // withdraw payout
    withdrawPayout(): void {
        if (this.walletBalance < 50) {
            this.snackBar.open('Minimum withdrawal amount is SGD 50.', 'Close', {
                duration: 3000,
                panelClass: ['error-snackbar']
            })
            return
        }

        /*
          this.artisteSvc.requestPayout(this.walletBalance).subscribe({
              next: () => {
                  this.snackBar.open('Payout request submitted successfully!', 'Close', {
                      duration: 3000,
                      panelClass: ['success-snackbar']
                  })
                  this.loadWalletData(); // Refresh data after payout request
              },
              error: () => {
                  this.snackBar.open('Failed to submit payout request. Please try again.', 'Close', {
                      duration: 3000,
                      panelClass: ['error-snackbar']
                  })
              }
          })
        */
    }
}