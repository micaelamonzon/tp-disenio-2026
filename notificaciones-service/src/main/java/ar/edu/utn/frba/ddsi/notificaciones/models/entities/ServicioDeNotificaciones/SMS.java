package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.TwilioSMSAdapter;
import ar.edu.utn.frba.ddsi.notificaciones.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMS implements MedioDeNotificacion {

  private final TwilioSMSAdapter twilioSMSAdapter;

  public SMS(TwilioSMSAdapter twilioSMSAdapter) {
    this.twilioSMSAdapter = twilioSMSAdapter;
  }

  @Override
  public void notificar(String destinatario, Notificacion notificacion) {
    twilioSMSAdapter.enviar(destinatario, notificacion.getMensaje());
  }
}