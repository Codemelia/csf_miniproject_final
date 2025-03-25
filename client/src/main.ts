import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';
import { environment } from './environments/environment';
import { enableProdMode } from '@angular/core';

// service worker
if ('serviceWorker' in navigator && environment.production) {
  navigator.serviceWorker.register('ngsw-worker.js')
    .then((registration) => {
      console.log('Service Worker registered with scope: ', registration.scope);
    })
    .catch((error) => {
      console.error('Service Worker registration failed: ', error);
    });
}


platformBrowserDynamic().bootstrapModule(AppModule, {
  ngZoneEventCoalescing: true,
})
  .catch(err => console.error(err));
