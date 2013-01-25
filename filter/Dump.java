package filter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import jpcap.*;
import jpcap.packet.*;

class Dump implements PacketReceiver {
	File f=new File("f:/text.txt");
	DataOutputStream ds;
	Dump(){
		try {
			ds=new DataOutputStream(new FileOutputStream(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void receivePacket(Packet packet) {
		printP(getType(packet),packet);   //��ӡЭ����ϸ��Ϣ
		//System.out.println(packet.sec+"  "+packet.usec);
		//System.out.println(packet);
	}
	public void printP(String p,Packet packet){//��ӡЭ����ϸ��Ϣ
		//System.out.println(p);
		/*if(!p.equals("ARP")){
			System.out.println(packet);
		}*/
		/*if(p.equals("ICMP")){
			System.out.println(packet);
			DBoperate_.insert("insert into r value('"+packet+"')");
		}*/
		if(p.equals("TCP") || p.equals("UDP")){
			printIP(packet);
		}
		/*
		else if(p.equals("UDP")){
			printUDP(packet);
		}*/
		
		/*if(p.equals("TCP")){
			printTCP(packet);
		}*/
	}
	public void printIP(Packet packet){
		IPPacket ipP=(IPPacket)packet;
		if(ipP.option!=null)
		{
			System.out.println((ipP.options));
			System.out.println(ipP);
		}
	}
	public void printTCP(Packet packet){   //��ӡtcp
		TCPPacket tcpP=(TCPPacket)packet;
		EthernetPacket eP=(EthernetPacket)packet.datalink;
		/*if(tcpP.dst_port==808 || tcpP.src_port==808 || tcpP.dst_port==80 || tcpP.src_port==80){
			return;
		}*/
		if(tcpP.dst_port!=80 && tcpP.src_port!=80){
			return;
		}
		/*if(!tcpP.src_ip.toString().equals("/172.16.2.14") && !tcpP.dst_ip.toString().equals("/172.16.2.14")){
			return;
		}*/
		Calendar ca=Calendar.getInstance();
		int h=ca.get(Calendar.HOUR_OF_DAY);
		int m=ca.get(Calendar.MINUTE);
		int s=ca.get(Calendar.SECOND);
		System.out.println(h+":"+m+":"+s);
		System.out.println("ԴIP:"+tcpP.src_ip+" Դ�˿�:"+tcpP.src_port+" Ŀ��IP:"+tcpP.dst_ip+"Ŀ�Ķ˿�:"+tcpP.dst_port);
		System.out.println("ԴMAC��ַ:"+eP.getSourceAddress()+" Ŀ��MAC��ַ:"+eP.getDestinationAddress());
		System.out.println("���ݳ��ȣ�"+tcpP.data.length);
		System.out.println("����: \n");
		for(int i=0;i<tcpP.data.length;i++){
			System.out.print((char)tcpP.data[i]);
		}
		System.out.println();
	}
	public void printUDP(Packet packet){   //��ӡudp
		UDPPacket udpP=(UDPPacket)packet;
		EthernetPacket eP=(EthernetPacket)packet.datalink;
		if(udpP.src_port!=520 && udpP.dst_port!=520)
			return;
		System.out.println("ԴIP:"+udpP.src_ip+" Դ�˿�:"+udpP.src_port+" Ŀ��IP:"+udpP.dst_ip+"Ŀ�Ķ˿�:"+udpP.dst_port);
		System.out.println("ԴMAC��ַ:"+eP.getSourceAddress()+" Ŀ��MAC��ַ:"+eP.getDestinationAddress());
		System.out.println("����: \n");
		for(int i=0;i<udpP.data.length;i++){
			System.out.print(udpP.data[i]+"  ");
		}
		System.out.println();
	}
	public String getType(Packet packet){  //��ȡЭ������
		String type=null;
		if(packet instanceof TCPPacket){
			type="TCP";
		}
		else if(packet instanceof UDPPacket){
			type="UDP";
		}
		else if(packet instanceof ARPPacket){
			type="ARP";
		}
		else if(packet instanceof ICMPPacket){
			type="ICMP";
		}
		else if(packet instanceof IPPacket){
			type="IP";
		}
		else type="δ֪";
		return type;
	}
	
	public void printAllByte(Packet packet){    //��ӡ�������ֽ�
		System.out.println(packet.caplen);
		int i=0;
		for(i=0;i<packet.data.length;i++){
			System.out.print(packet.data[i]+"_");
		}
		System.out.println();
		System.out.println(i);
		for(i=0;i<packet.header.length;i++){
			System.out.print(packet.header[i]+"_");
		}
		System.out.println();
		System.out.println(i);
	}
	public void print(NetworkInterface []devices){   //��ӡ���� ��Ϣ
		System.out.println("usage: java Tcpdump <select a number from the following>");
		
		for (int i = 0; i < devices.length; i++) {
			System.out.println(i+" :"+devices[i].name + "(" + devices[i].description+")");
			System.out.println("    data link:"+devices[i].datalink_name + "("
					+ devices[i].datalink_description+")");
			System.out.print("    MAC address:");
			for (byte b : devices[i].mac_address)
				System.out.print(Integer.toHexString(b&0xff) + ":");
			System.out.println();
			for (NetworkInterfaceAddress a : devices[i].addresses)
				System.out.println("    address:"+a.address + " " + a.subnet + " "
						+ a.broadcast);
		}
	}
	public static void main(String[] args) throws Exception {
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		JpcapCaptor jpcap = JpcapCaptor.openDevice(devices[1], 2000, true, 20);
		Dump d=new Dump();
		jpcap.loopPacket(-1, d);
	}
}
