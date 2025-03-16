import { Component, Inject, Input } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-success-popup',
  standalone: false,
  templateUrl: './success-popup.component.html',
  styleUrl: './success-popup.component.scss'
})
export class SuccessPopupComponent {

  successMessage: string = 'Payment successful'
  artisteThankYouMessage: string = ''

  constructor(
    public dialogRef: MatDialogRef<SuccessPopupComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { artisteThankYouMessage: string }
  ) {
    // Assign thank-you message from data
    this.artisteThankYouMessage = data.artisteThankYouMessage || 'Thank you for supporting our Vibees!'
  }

  ngOnInit(): void {
    // auto-close after 10 seconds
    setTimeout(() => {
      this.dialogRef.close()
    }, 10000);
  }

  // close pop up
  closePopup(): void {
    this.dialogRef.close()
  }

}
