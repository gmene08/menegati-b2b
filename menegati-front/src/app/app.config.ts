import { ApplicationConfig, inject, LOCALE_ID, provideAppInitializer, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { cookieInterceptor } from './core/interceptors/cookie-interceptor';
import { AuthService } from './core/services/auth.service';

registerLocaleData(localePt);

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    { provide: LOCALE_ID, useValue: 'pt' },
    provideHttpClient(withInterceptors([cookieInterceptor])),

    provideAppInitializer(()=>{
      const authService = inject(AuthService);
      return authService.validateSession();
    })
  ]
};
