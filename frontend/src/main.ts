import { bootstrapApplication } from "@angular/platform-browser";
import { provideHttpClient } from "@angular/common/http";
import { AppComponent, API_URL } from "./app";
fetch("/config.json")
  .then((r) => {
    if (!r.ok) throw Error("Configuración no disponible");
    return r.json();
  })
  .then((config) =>
    bootstrapApplication(AppComponent, {
      providers: [
        provideHttpClient(),
        { provide: API_URL, useValue: config.apiUrl },
      ],
    }),
  )
  .catch(() => {
    document.body.textContent =
      "No se pudo iniciar Taskflow. Recarga la página.";
  });
