# chatcito-server

## Verificación de Métricas - Grafana

Las siguientes métricas de WebSocket se monitorizan en Grafana:

- **ws_messages_broadcast_total**: Total de mensajes enviados por broadcast en tiempo real
![](/server/src/main/resources/ws_messages_broadcast_total.png)

- **ws_sessions_active**: Número de sesiones WebSocket activas conectadas
![](/server/src/main/resources/ws_sessions_active.png)

- **ws_messages_received_total**: Total de mensajes recibidos por el servidor
![](/server/src/main/resources/ws_messages_received_total.png)
Estas métricas confirman el correcto funcionamiento del servidor de chat y permiten monitorear la actividad de conexiones en tiempo real.

---
