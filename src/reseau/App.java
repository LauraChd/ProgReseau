package reseau;
import java.net.InetAddress;

public class App {
    public static void main(String[] args) throws Exception {

        //getLocalHost = @IP de la machine utilisée
        InetAddress ad = InetAddress.getLocalHost();
        System.out.println(ad);

        InetAddress[] fullAd = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
        System.out.println("fullAd = \n");
        for(InetAddress a : fullAd){
            System.out.println(a.getHostName() + a.getHostAddress() + "\n");
        }

        //getByName = construit l'InetAdress de la machine donnée en paramètre, sous forme d'@IP ou de nom
        InetAddress google = InetAddress.getByName("google.com");
        System.out.println(google);

        InetAddress[] fullGoogle = InetAddress.getAllByName("google.com");
        System.out.println("fullGoogle = \n");
        for(InetAddress a : fullGoogle){
            System.out.println(a.getHostName() + a.getHostAddress() + "\n");
        }

        InetAddress googleBis = InetAddress.getByName("172.217.20.206");
        System.out.println(googleBis.getHostName());

        InetAddress unknown = InetAddress.getByName("fhrgiv.com");
        System.out.println(unknown);
    }
}
