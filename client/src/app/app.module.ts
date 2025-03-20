import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { ArtisteListComponent } from './components/home/artiste-list.component';
import { TipFormComponent } from './components/tip-a-vibee/tip-form.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { LoginComponent } from './components/authentication/login.component';
import { RegisterComponent } from './components/authentication/register.component';

import { RouterModule, Routes } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTableModule } from '@angular/material/table';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinner } from '@angular/material/progress-spinner'
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatMenuModule } from '@angular/material/menu';
import { MatListModule } from '@angular/material/list';

import { RoleGuard } from './guards/role.guard';
import { AuthGuard } from './guards/auth.guard';
import { WalletComponent } from './components/dashboard/wallet.component';
import { ProfileComponent } from './components/dashboard/profile.component';
import { ArtisteQuizComponent } from './components/dashboard/artiste-quiz.component';
import { SuccessPopupComponent } from './components/tip-a-vibee/success-popup.component';
import { OverviewComponent } from './components/dashboard/overview.component';
import { TipsHistoryComponent } from './components/dashboard/tips-history.component';
import { AuthStore } from './stores/auth.store';
import { provideComponentStore } from '@ngrx/component-store';
import { MatButtonToggleModule } from '@angular/material/button-toggle';

const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'home', component: HomeComponent, canActivate: [ AuthGuard ] },
  { path: 'vibees', component: ArtisteListComponent, canActivate: [ AuthGuard ] },
  { path: 'tip-form', component: TipFormComponent }, // permit all access
  { path: 'tip-form/:artisteStageName', component: TipFormComponent }, // allow path variable to be passed in
  { path: 'dashboard', component: DashboardComponent, 
    canActivate: [ AuthGuard, RoleGuard ],
    data: { expectedRole: 'ARTISTE' },
    children: [
      { path: 'overview', component: OverviewComponent },
      { path: 'tips-history', component: TipsHistoryComponent },
      { path: 'wallet', component: WalletComponent },
      { path: 'profile', component: ProfileComponent },
      { path: '', redirectTo: 'overview', pathMatch: 'full' } // Default route
    ] },
  { path: '**', redirectTo: '/', pathMatch: 'full' }
]

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    ArtisteListComponent,
    TipFormComponent,
    DashboardComponent,
    LoginComponent,
    RegisterComponent,
    WalletComponent,
    ProfileComponent,
    ArtisteQuizComponent,
    SuccessPopupComponent,
    OverviewComponent,
    TipsHistoryComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes, { useHash: true }),
    BrowserAnimationsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatToolbarModule,
    MatTableModule,
    MatOptionModule,
    MatSelectModule,
    MatTabsModule,
    MatProgressBarModule,
    FormsModule,
    MatProgressSpinner,
    MatExpansionModule,
    MatIconModule,
    MatDialogModule,
    MatSidenavModule,
    MatMenuModule,
    MatListModule,
    MatButtonToggleModule
  ],
  providers: [
    provideHttpClient(),
    provideAnimationsAsync(),
    provideComponentStore(AuthStore)
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
