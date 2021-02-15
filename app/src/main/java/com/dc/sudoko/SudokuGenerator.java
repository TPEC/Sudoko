package com.dc.sudoko;

import android.util.Pair;

import java.util.*;

public class SudokuGenerator {
    private static String last_difficulty = "";

    private static final int[][] BASE = {
//            {
//                    1, 2, 3, 4, 5, 6, 7, 8, 9,
//                    4, 5, 6, 7, 8, 9, 1, 2, 3,
//                    7, 8, 9, 1, 2, 3, 4, 5, 6,
//                    2, 1, 4, 3, 6, 5, 8, 9, 7,
//                    3, 6, 5, 8, 9, 7, 2, 1, 4,
//                    8, 9, 7, 2, 1, 4, 3, 6, 5,
//                    5, 3, 1, 6, 4, 2, 9, 7, 8,
//                    6, 4, 2, 9, 7, 8, 5, 3, 1,
//                    9, 7, 8, 5, 3, 1, 6, 4, 2,
//            },
            {
                    7, 6, 1, 9, 3, 4, 8, 2, 5,
                    3, 5, 4, 6, 2, 8, 1, 9, 7,
                    9, 2, 8, 1, 5, 7, 6, 3, 4,
                    2, 1, 9, 5, 4, 6, 3, 7, 8,
                    4, 8, 3, 2, 7, 9, 5, 1, 6,
                    5, 7, 6, 3, 8, 1, 9, 4, 2,
                    1, 9, 5, 7, 6, 2, 4, 8, 3,
                    8, 3, 2, 4, 9, 5, 7, 6, 1,
                    6, 4, 7, 8, 1, 3, 2, 5, 9,
            },
            {
                    1, 4, 9, 8, 3, 6, 7, 5, 2,
                    5, 7, 6, 2, 4, 1, 9, 3, 8,
                    2, 3, 8, 5, 7, 9, 1, 6, 4,
                    7, 2, 4, 3, 6, 8, 5, 9, 1,
                    6, 8, 3, 9, 1, 5, 4, 2, 7,
                    9, 5, 1, 4, 2, 7, 3, 8, 6,
                    3, 6, 2, 7, 9, 4, 8, 1, 5,
                    4, 1, 5, 6, 8, 3, 2, 7, 9,
                    8, 9, 7, 1, 5, 2, 6, 4, 3,
            },
            {
                    8, 3, 9, 6, 5, 7, 2, 1, 4,
                    6, 7, 2, 9, 4, 1, 5, 8, 3,
                    1, 5, 4, 8, 3, 2, 9, 6, 7,
                    5, 4, 1, 2, 8, 3, 7, 9, 6,
                    2, 8, 7, 4, 9, 6, 3, 5, 1,
                    9, 6, 3, 7, 1, 5, 4, 2, 8,
                    7, 1, 8, 3, 2, 9, 6, 4, 5,
                    3, 2, 5, 1, 6, 4, 8, 7, 9,
                    4, 9, 6, 5, 7, 8, 1, 3, 2,
            },
            {
                    4, 8, 3, 2, 7, 1, 6, 9, 5,
                    9, 7, 6, 4, 8, 5, 3, 2, 1,
                    5, 2, 1, 3, 9, 6, 4, 7, 8,
                    2, 9, 4, 6, 5, 8, 1, 3, 7,
                    1, 3, 8, 9, 2, 7, 5, 6, 4,
                    6, 5, 7, 1, 3, 4, 9, 8, 2,
                    8, 4, 2, 5, 6, 3, 7, 1, 9,
                    3, 1, 9, 7, 4, 2, 8, 5, 6,
                    7, 6, 5, 8, 1, 9, 2, 4, 3,
            }
    };

    private static final int[] BLOCK_OFFSET_OUTSIDE = {
            0, 3, 6,
            27, 30, 33,
            54, 57, 60
    };

    private static final int[] BLOCK_OFFSET_INSIDE = {
            0, 1, 2,
            9, 10, 11,
            18, 19, 20
    };

    private static final Random R = new Random(System.currentTimeMillis());

    public static String getLastDifficulty() {
        return last_difficulty;
    }

    public static int[] create(int round) {
        int[] s = Arrays.copyOf(BASE[R.nextInt(BASE.length)], 81);
        while (--round >= 0) {
            int opt = R.nextInt(4);
            int r1 = R.nextInt(3);
            int r2 = (r1 + R.nextInt(2) + 1) % 3;
//            int rb = R.nextInt(3) * 3;
            switch (opt) {
                case 0:
                    for (int rb = 0; rb < 9; rb += 3) {
                        swap_row(s, rb + r1, rb + r2);
                    }
                    break;
                case 1:
                    for (int rb = 0; rb < 9; rb += 3) {
                        swap_col(s, rb + r1, rb + r2);
                    }
                    break;
                case 2:
                    for (int i = 0; i < 3; ++i) {
                        swap_row(s, r1 * 3 + i, r2 * 3 + i);
                    }
                    break;
                case 3:
                    for (int i = 0; i < 3; ++i) {
                        swap_col(s, r1 * 3 + i, r2 * 3 + i);
                    }
                    break;
            }
        }
        return s;
    }

    public static int[] mask(int[] s, int p1, int p2) {
        int[] mask = new int[81];
        Arrays.fill(mask, 1);
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < 81; ++i) {
            order.add(i);
        }
        Collections.shuffle(order, R);
        int c1 = 0, c2 = 0;
        for (Integer o : order) {
            if (getPossibleCount(s, mask, o) == 1) {
                if (--p1 >= 0) {
                    mask[o] = 0;
                    ++c1;
                }
            }
        }
        List<Pair<Integer, Integer>> possibles = new ArrayList<>();
        for (int i = 0; i < 81; ++i) {
            if (mask[i] > 0) {
                int p = getPossibleCount(s, mask, i);
                if (p > 1) {
                    possibles.add(new Pair<>(i, p));
                }
            }
        }
        Collections.sort(possibles, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                return Integer.compare(o2.second, o1.second);
            }
        });
        if (p2 > 0) {
            for (Pair<Integer, Integer> e : possibles) {
                int[] m = merge(s, mask);
                Set<Integer> ps = getPossible(m, e.first);
                ps.remove(s[e.first]);
                boolean flag = true;
                for (Integer p : ps) {
                    m[e.first] = p;
                    if (solve(m, 0)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    mask[e.first] = 0;
                    ++c2;
                    if (--p2 <= 0) {
                        break;
                    }
                }
            }
        }
        last_difficulty = c1 + ", " + c2 + "/" + possibles.size();
        return mask;
    }

    public static int getPossibleCount(int[] s, int[] mask, int index) {
        Set<Integer> p = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        int row = index / 9;
        int col = index % 9;
        int blk = (row / 3) * 3 + col / 3;
        for (int i = 0; i < 9; ++i) {
            int i1 = i * 9 + col;
            int i2 = row * 9 + i;
            int i3 = BLOCK_OFFSET_OUTSIDE[blk] + BLOCK_OFFSET_INSIDE[i];
            if (i1 != index && mask[i1] > 0) {
                p.remove(s[i1]);
            }
            if (i2 != index && mask[i2] > 0) {
                p.remove(s[i2]);
            }
            if (i3 != index && mask[i3] > 0) {
                p.remove(s[i3]);
            }
        }
        return p.size();
    }

    public static Set<Integer> getPossible(int[] s, int index) {
        Set<Integer> p = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        int row = index / 9;
        int col = index % 9;
        int blk = (row / 3) * 3 + col / 3;
        for (int i = 0; i < 9; ++i) {
            int i1 = i * 9 + col;
            int i2 = row * 9 + i;
            int i3 = BLOCK_OFFSET_OUTSIDE[blk] + BLOCK_OFFSET_INSIDE[i];
            if (i1 != index) {
                p.remove(s[i1]);
            }
            if (i2 != index) {
                p.remove(s[i2]);
            }
            if (i3 != index) {
                p.remove(s[i3]);
            }
        }
        return p;
    }

    public static boolean solve(int[] s, int from_index) {
        boolean solved = true;
        for (int i = from_index; i < 81; ++i) {
            if (s[i] == 0) {
                solved = false;
                Set<Integer> ps = getPossible(s, i);
                for (Integer p : ps) {
                    s[i] = p;
                    if (solve(s, i + 1)) {
                        return true;
                    }
                }
                s[i] = 0;
                break;
            }
        }
        return solved;
    }

    public static int[] merge(int[] s, int[] mask) {
        int[] r = new int[81];
        for (int i = 0; i < 81; ++i) {
            if (mask[i] > 0) {
                r[i] = s[i];
            } else {
                r[i] = 0;
            }
        }
        return r;
    }

    public static void print(int[] s) {
        System.out.println("--------------------");
        for (int i = 0; i < s.length; ++i) {
            if (s[i] == 0) {
                System.out.print("_ ");
            } else {
                System.out.print(s[i] + " ");
            }
            if ((i + 1) % 9 == 0) {
                System.out.println();
            }
        }
    }

    private static void swap_row(int[] s, int r1, int r2) {
        for (int i = 0; i < 9; ++i) {
            int t = s[r1 * 9 + i];
            s[r1 * 9 + i] = s[r2 * 9 + i];
            s[r2 * 9 + i] = t;
        }
    }

    private static void swap_col(int[] s, int c1, int c2) {
        for (int i = 0; i < 9; ++i) {
            int t = s[i * 9 + c1];
            s[i * 9 + c1] = s[i * 9 + c2];
            s[i * 9 + c2] = t;
        }
    }
}
