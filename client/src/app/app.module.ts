import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { HomeComponent } from './components/main/home.component';
import { ArtisteListComponent } from './components/main/artiste-list.component';
import { TipComponent } from './components/main/tip.component';
import { DashboardComponent } from './components/main/dashboard.component';
import { LoginComponent } from './components/authentication/login.component';
import { RegisterComponent } from './components/authentication/register.component';

import { RouteReuseStrategy, RouterModule, Routes } from '@angular/router';
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

import { RoleGuard } from './guards/role.guard';
import { AuthGuard } from './guards/auth.guard';
import { WalletComponent } from './components/main/wallet.component';
import { CarouselComponent } from './components/unused/carousel.component';
import { ProfileComponent } from './components/main/profile.component';
import { ArtisteQuizComponent } from './components/main/artiste-quiz.component';

const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'home', component: HomeComponent, canActivate: [ AuthGuard ] },
  { path: 'artistes', component: ArtisteListComponent, canActivate: [ AuthGuard ] },
  { path: 'tip', component: TipComponent, canActivate: [ AuthGuard ] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [ AuthGuard, RoleGuard ],
    data: { expectedRole: 'ARTISTE' } },
  { path: 'wallet/:artisteId', component: WalletComponent, canActivate: [ AuthGuard, RoleGuard ],
    data: { expectedRole: 'ARTISTE' } },
  { path: 'profile/:artisteId', component: ProfileComponent, canActivate: [ AuthGuard, RoleGuard ],
    data: { expectedRole: 'ARTISTE' } },
  { path: '**', redirectTo: '/', pathMatch: 'full' }
]

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    ArtisteListComponent,
    TipComponent,
    DashboardComponent,
    LoginComponent,
    RegisterComponent,
    WalletComponent,
    CarouselComponent,
    ProfileComponent,
    ArtisteQuizComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
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
    MatProgressSpinner
  ],
  providers: [
    provideHttpClient(),
    provideAnimationsAsync()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
