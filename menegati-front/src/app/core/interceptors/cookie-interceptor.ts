import { HttpInterceptorFn, HttpRequest } from '@angular/common/http';

export const cookieInterceptor: HttpInterceptorFn = (req, next) => {

  const secureReq = req.clone({
    withCredentials: true,
  });

  return next(secureReq);
};
