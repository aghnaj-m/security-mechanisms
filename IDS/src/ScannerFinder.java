import java.util.HashMap;
import java.util.Map;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

public class ScannerFinder {

	Map<String, Integer> syn = new HashMap <String, Integer>();
	Map<String, Integer> syn_ack = new HashMap <String, Integer>();
	
	public ScannerFinder(final String path) {
		// Stringbuilder est utilisé pour obtenir des messages d’erreur en cas d’erreur se produit
		final StringBuilder errbuf = new StringBuilder();
	
		// Faire de l’objet Pcap un fichier pcap d’ouverture en mode hors ligne 
		// et passer le nom de fichier pcap et l’objet Stringbuilder à la fonction
		final Pcap pcap = Pcap.openOffline(path, errbuf);
		if (pcap == null) {
			System.err.println(errbuf);
			return;
		}
		
		//la lecture des paquets
		pcap.loop(Pcap.LOOP_INFINITE, new JPacketHandler<StringBuilder>() {

			final Tcp tcp = new Tcp();
			final Ip4 ip = new Ip4();

			public void nextPacket(JPacket packet, StringBuilder errbuf) {
				if (packet.hasHeader(tcp) && packet.hasHeader(ip) && tcp.flags_SYN() && tcp.flags_ACK()) { 
					//si le paquet contient SYN et ACK les deux
					//incrementer SYN ACK des adresses ip de destination
					String synackip = org.jnetpcap.packet.format.FormatUtils.ip(ip.destination());
					if(syn_ack.containsKey(synackip))
						syn_ack.put(synackip, syn_ack.get(synackip)+1);
					else
						syn_ack.put(synackip, 1);
				}else if(packet.hasHeader(tcp) && packet.hasHeader(ip) && tcp.flags_SYN()){ //The Packet is just SYN
					//Increment the source ip's SYN count
					String synip = org.jnetpcap.packet.format.FormatUtils.ip(ip.source());
					if(syn.containsKey(synip))
						syn.put(synip, syn.get(synip)+1);
					else
						syn.put(synip, 1);
				}
			}
		}, errbuf);
		pcap.close();
		
		for(String ssyn : syn.keySet()) //Pour tous les adresses IP qui ont envoyé SYN
			if(syn_ack.containsKey(ssyn)) { //Vérification s’il y a des paquets SYN ACK envoyés
				if(syn.get(ssyn) > 3*syn_ack.get(ssyn)) 
					System.out.println(ssyn);
			}else //sinon le serveur n'a pas répondu
				System.out.println(ssyn);
	}
	public static void main(String[] args) {
		new ScannerFinder("./portscan.pcap");
	}
}
