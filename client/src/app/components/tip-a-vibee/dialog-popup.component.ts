import { Component, Inject, Input } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-dialog-popup',
  standalone: false,
  templateUrl: './dialog-popup.component.html',
  styleUrl: './dialog-popup.component.scss'
})
export class DialogPopupComponent {

  artisteThankYouMessage: string = ''
  isLoading: boolean = false;
  isSuccess: boolean = false

  constructor(
    public dialogRef: MatDialogRef<DialogPopupComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { artisteThankYouMessage: string, isLoading: boolean, isSuccess: boolean }
  ) {
    // assign values from data
    this.artisteThankYouMessage = data.artisteThankYouMessage || 'Thank you for supporting our Vibees!'
    this.isLoading = data.isLoading || false
    this.isSuccess = data.isSuccess || false
  }

  // close pop up
  closePopup(): void {
    this.dialogRef.close()
  }

}
