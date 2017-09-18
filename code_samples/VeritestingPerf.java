// x = array of symbolic integers
// len = concrete length of x
sum = 0;
for (int i = 0; i < len; i++) {
  // Begin region for static unrolling
  if (x[i] < 0) sum += -1;
  else if (x[i] > 0) sum += 1;
  // End region for static unrolling
}
if (sum < 0) { // handles negative sum
} else if (sum > 0) { // handles positive sum
} else { // contains a bug
}
