import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { ArtisteListComponent } from './components/vibee-list/artiste-list.component';
import { TipFormComponent } from './components/tip-a-vibee/tip-form.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { LoginComponent } from './components/authentication/login.component';
import { RegisterComponent } from './components/authentication/register.component';
import { ProfileEditComponent } from './components/dashboard/profile-edit.component';
import { ArtisteQuizComponent } from './components/dashboard/artiste-quiz.component';
import { DialogPopupComponent } from './components/tip-a-vibee/dialog-popup.component';
import { OverviewComponent } from './components/dashboard/overview.component';
import { TipsHistoryComponent } from './components/dashboard/tips-history.component';

import { RouterModule, Routes } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
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
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';

import { RoleGuard } from './guards/role.guard';
import { AuthGuard } from './guards/auth.guard';
import { AuthStore } from './stores/auth.store';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { ArtisteService } from './services/artiste.service';
import { TipService } from './services/tip.service';
import { SpotifyService } from './services/spotify.service';
import { ServiceWorkerModule } from '@angular/service-worker';

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
      { path: 'profile-edit', component: ProfileEditComponent },
      { path: '**', redirectTo: 'overview', pathMatch: 'full' } // default route
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
    ProfileEditComponent,
    ArtisteQuizComponent,
    DialogPopupComponent,
    OverviewComponent,
    TipsHistoryComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    BrowserAnimationsModule,
    ReactiveFormsModule,
    FormsModule,
    CommonModule,
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
    MatProgressSpinner,
    MatExpansionModule,
    MatIconModule,
    MatDialogModule,
    MatSidenavModule,
    MatMenuModule,
    MatListModule,
    MatButtonToggleModule,
    MatSortModule,
    MatPaginatorModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatChipsModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      registrationStrategy: 'registerWhenStable:30000'
    })
  ],
  providers: [
    provideHttpClient(),
    provideAnimationsAsync(),
    AuthStore,
    ArtisteService,
    TipService,
    SpotifyService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
