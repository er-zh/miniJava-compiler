class HanoiDemo {
	public static void main (String [] argv) {
		{
			// Statements
			// `dummy` is a variable.
			// It is not declared but the syntax parser doesn't care about that.
			// In this way the program can survive parsing.
			Hanoi h = new Hanoi();
			h.start(8);

			/* The class `Numbers` is in the second sample code
			 * This also poses no syntax errors but is semantically incorrect
			 */
			
			Numbers n = new Numbers();
			System.out.println(n.choose(10, 5));
		}
	}
}

class Hanoi {
	public int start(int n) {
		
		return this.recursive(n, 1, 2, 3);
	}
	public int recursive(int n, int start, int middle, int end) {
		if (n <= 1) {
			System.out.println(start);
			System.out.println(end);
		} else {
			// move 1 to n-1 from start to middle
			int h = this.recursive(n-1, start, end, middle);
			// move n from start to end
			System.out.println(n);
			System.out.println(start);
			System.out.println(end);
			// move 1 to n-1 from middle to end
			h = this.recursive(n-1, middle, start, end);
		}
		return 12;
	}
}

class Numbers {
	public int choose(int n, int k) {
		int res = 0;
        int i = 0;
		if (!(n<k)) {
			// n! / (k! (n-k)!)
			i = 1;
			res = -1; // just to test unary -
			while (i <= k) {
				res = res * (n-k+i) / i;
			}
		} else {
			res = 0;
		}
		return res;
	}

	public int gcd(int a, int b) {
		int tmp = 0;
        int res = 0;
		if (a < b) {
			tmp = a;
			a = b;
			b = tmp;
		} else {
		}
		if (b == 0)
			res = a;
		else if ((a / b) * b != a) {
			res = this.gcd(b, a-b*(a/b));
		} else {
			res = b;
		}
		return res;
	}
}
