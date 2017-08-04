// x = array of symbolic integers
// len = concrete length of x
for (int i = 0; i < len; i++) {
  // Begin region for static unrolling
  if (x[i] < 0) sum += -1;
  else if (x[i] > 0) sum += 1;
  // End region for static unrolling
}
if (sum < 0) System.out.println("neg");
else if (sum > 0) System.out.println("pos");
else System.out.println("bug");
