package com.exact.sendemail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.exact.bean.ReporteRecepcionBean;
import com.exact.bean.UsuarioCorreoBean;
import com.exact.dao.ReporteRecepcionDAO;

public class JavaMailRecepcion {

	public static void main(String[] args) {

		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		// String remitente = "";
		String htmlRecepcionCliente = "";
		String htmlRecojoCliente = "";

		UsuarioCorreoBean usuarioAdmin = Metodos.GetAdministrador();
		List<UsuarioCorreoBean> lClientes = Metodos.GetCorreosClientes();
		ReporteRecepcionBean lReportes = Metodos.ObtenerObjetoReporteRecepcion();
		htmlRecepcionCliente = Metodos.obtenerEstructuraRecepcionCliente(lReportes);
		htmlRecojoCliente = Metodos.obtenerEstructuraHtmlRecojoCliente(lReportes);

		// El servidor host
		String host = "smtp.gmail.com";

		// Obtener las propiedades del sistema
		Properties propiedades = System.getProperties();

		// Configuramos el servidor de correo
		propiedades.setProperty("mail.smtp.host", host);
		propiedades.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		propiedades.setProperty("mail.smtp.socketFactory.fallback", "false");
		propiedades.setProperty("mail.smtp.port", "465");
		propiedades.setProperty("mail.smtp.socketFactory.port", "465");
		propiedades.put("mail.smtp.auth", "true");
		propiedades.put("mail.debug", "true");
		propiedades.put("mail.store.protocol", "pop3");
		propiedades.put("mail.transport.protocol", "smtp");

		final String clave = usuarioAdmin.getsClave();
		final String remitente = usuarioAdmin.getCorreo();

		// Obtenemos la session por defecto
		Session session = Session.getDefaultInstance(propiedades, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(remitente, clave);
			}
		});

		try {

			for (UsuarioCorreoBean usuarioCorreoBean : lClientes) {

				if (lReportes == null)
					return;
				Message mensaje = new MimeMessage(session);
				mensaje.setFrom(new InternetAddress(remitente));
				mensaje.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(usuarioCorreoBean.getCorreo(), false));
				mensaje.setSubject("Reporte de recojo y recepción de valijas");
				mensaje.setContent(
						htmlRecojoCliente + htmlRecepcionCliente + "</br>"
								+ "<p style='font-size=15px'>Para mayor información visitanos en <a href='http://www.exact.com.pe/rvaws/#/'>http://www.exact.com.pe/rvaws/#/login.html</a></p>"
								+"</br>"
								+"--"
								+"</br>"
								+Metodos.ObtenerFirma(),
						"text/html");
				mensaje.setSentDate(new Date());
				Transport.send(mensaje);
				System.out.println("Mensaje enviado");

			}

		} catch (Exception e) {
			try {
				FileWriter fw = new FileWriter(new File("archivo.txt"));
				fw.write(e.getMessage());
				fw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}

	}
}
