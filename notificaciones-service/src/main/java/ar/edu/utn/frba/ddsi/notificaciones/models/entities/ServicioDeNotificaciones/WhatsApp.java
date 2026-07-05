package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.TwilioWhatsAppAdapter;
import ar.edu.utn.frba.ddsi.notificaciones.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WhatsApp implements MedioDeNotificacion {

  private final TwilioWhatsAppAdapter twilioWhatsAppAdapter;

  public WhatsApp(TwilioWhatsAppAdapter twilioWhatsAppAdapter) {
    this.twilioWhatsAppAdapter = twilioWhatsAppAdapter;
  }

  @Override
  public void notificar(String destinatario, Notificacion notificacion) {
    twilioWhatsAppAdapter.enviar(destinatario, notificacion.getMensaje());
  }
}