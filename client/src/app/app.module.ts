import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { HomeComponent } from './components/home.component';
import { MusicianListComponent } from './components/musician-list.component';
import { TipCouponComponent } from './components/tip-coupon.component';
import { DashboardComponent } from './components/dashboard.component';
import { RouterModule, Routes } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'musicians', component: MusicianListComponent },
  { path: 'tip', component: TipCouponComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: '**', redirectTo: '', pathMatch: 'full' }
]

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    MusicianListComponent,
    TipCouponComponent,
    DashboardComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    BrowserAnimationsModule,
    MatToolbarModule,
    MatButtonModule
  ],
  providers: [
    provideHttpClient()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
