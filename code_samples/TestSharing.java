public class TestSharing {
	public static void testSharing(int x, int bound) {
    for(int i=0; i < bound; i++) x = x + x;
    if ( x < -50 || x > 50) System.out.println("then side");
    else System.out.println("else side");
	}
	public static void main(String[] args) {
		testSharing(0, Integer.parseInt(args[0]));
	}
}
