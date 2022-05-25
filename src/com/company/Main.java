package com.company;

import java.util.ArrayList;
import java.util.Locale;

import static java.lang.Math.abs;

class Main {
    public static void main(String[] args) {
        Desk d = new Desk();
        Desk.color = -1;
        d.correctMoves();
        d.convertFen("rnbqkbnrpppppppp                                PPPPPPPPRNBQKBNR");
        d.printBoard();}
}

class Square {
    int i;
    int j;

    public Square(int i, int j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public String toString() {
        return "Square{" +
                "i=" + i +
                ", j=" + j +
                '}';
    }
}

class Move {
    Square from;
    Square to;

    public Move(Square from, Square to) {
        this.from = from;
        this.to = to;
    }

    public Move(int from_i, int from_j, int i, int j) {
        this.from = new Square(from_i, from_j);
        this.to = new Square(i, j);
    }

    @Override
    public String toString() {
        return "Move{" +
                "(" + from.i + ", " + from.j + ") -> (" +
                "(" + to.i + ", " + to.j + ")}";
    }
}

class Desk {
    boolean whiteUnderCheck = false;
    boolean blackUnderCheck = false;

    String[][] boardArray = {
            {"r", "n", "b", "q", "k", "b", "n", "r"},
            {"p", "p", "p", "p", "p", "p", "p", "p"},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"P", "P", "P", "P", "P", "P", "P", "P"},
            {"R", "N", "B", "Q", "K", "B", "N", "R"},
            {""}};

    public static int color = 1;


    public String[][] convertFen(String fen){
        int countToEight = -1;
        int j = 0;
        for (int i = 0; i < 63; i++){
            if (countToEight == 7){
                j += 1;
                countToEight = 0;
            }else{
                countToEight += 1;
            }
            boardArray[j][countToEight] = String.valueOf(fen.charAt(i));
        }
        return boardArray;
    }

    public void printBoard() {
        for (String[] line : boardArray) {
            for (String i : line)
                if (i.length() == 0)
                    System.out.print("  ");
                else
                    System.out.print(i + " ");
            System.out.println();
        }
    }

    public void move(int last_i, int last_j, int i, int j) {
        boardArray[i][j] = boardArray[last_i][last_j];
        boardArray[last_i][last_j] = "";
    }

    public void makeMove(int last_i, int last_j, int i, int j) {
        System.out.println(last_i);
        System.out.println(last_j);
        System.out.println(i);
        System.out.println(j);
        String taken_piece = boardArray[last_i][last_j];
        System.out.println(taken_piece);
        if (taken_piece.toLowerCase(Locale.ROOT).equals("p")
                && checkPawn(last_i, last_j, i, j, color)) {
            move(last_i, last_j, i, j);
            if (i == 0 && color == 1) {
                boardArray[i][j] = "Q";
            } else if (i == 7 && color == -1) {
                boardArray[i][j] = "q";
            }

            color = -color;
        }

        if (taken_piece.toLowerCase(Locale.ROOT).equals("n") && checkKnight(last_i, last_j, i, j, color)) {
            if (Character.isLowerCase(boardArray[i][j].charAt(0)) && color == 1) {
                move(last_i, last_j, i, j);
                color = -color;
            } else if (Character.isUpperCase(boardArray[i][j].charAt(0)) && color == -1) {
                move(last_i, last_j, i, j);
                color = -color;
            }
        }
        if (taken_piece.toLowerCase(Locale.ROOT).equals("b") && checkBishop(last_i, last_j, i, j, color)) {
            move(last_i, last_j, i, j);
            color = -color;
        }
        if (taken_piece.toLowerCase(Locale.ROOT).equals("r") && checkRook(last_i, last_j, i, j, color)) {
            move(last_i, last_j, i, j);
            color = -color;
        }
        if (taken_piece.toLowerCase(Locale.ROOT).equals("q") && checkQueen(last_i, last_j, i, j, color)) {
            move(last_i, last_j, i, j);
            color = -color;
        }
    }

    public boolean eats(int i, int j, int color) {
        if (color > 0)
            return (boardArray[i][j].length() == 0) || Character.isLowerCase(boardArray[i][j].charAt(0));
        else
            return (boardArray[i][j].length() == 0) || Character.isUpperCase(boardArray[i][j].charAt(0));
    }

    public boolean eatsEverything(int i, int j) {
        return boardArray[i][j].length() != 0;
    }

    public boolean checkKnight(int last_i, int last_j, int i, int j, int color) {
        int dx = abs(i - last_i);
        int dy = abs(j - last_j);
        return (((dx == 1 && dy == 2) || (dx == 2 && dy == 1)) && eats(i, j, color));
    }

    public boolean checkKing(int last_i, int last_j, int i, int j, int color) {
        return (abs(last_i - i) <= 1 && abs(last_j - j) <= 1) && eats(i, j, color);
    }

    public boolean checkBishop(int last_i, int last_j, int i, int j, int color) {
        if ((abs(i - last_i) == abs(j - last_j)) && eats(i, j, color)) {
            if (i > last_i) {
                if (j > last_j) {
                    for (int i5 = 1; i5 < j - last_j; i5++) {
                        if (eatsEverything(last_i + i5, last_j + i5)) {
                            return false;
                        }
                    }
                } else {
                    for (int i2 = 1; i2 < last_j - j; i2++) {
                        if (eatsEverything(last_i + i2, last_j - i2)) {
                            return false;
                        }
                    }
                }
            } else {
                if (j > last_j) {
                    for (int i3 = 1; i3 < j - last_j; i3++) {
                        if (eatsEverything(last_i - i3, last_j + i3)) {
                            return false;
                        }
                    }

                } else {
                    for (int i4 = 1; i4 < last_j - j; i4++) {
                        if (eatsEverything(last_i - i4, last_j - i4)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean checkPawn(int last_i, int last_j, int i, int j, int color) {
//        for (String[] strings : boardArray) {
//            System.out.println(Arrays.toString(strings));
//        }
        if (last_i == i + (2 * color) && (last_i == 1 || last_i == 6 * color) && boardArray[i][last_j].equals("") && last_j == j) {
            return true;
        } else if (last_i == i + color && boardArray[i][last_j].equals("") && last_j == j) {
            return true;
        } else return last_i == i + color && (j == last_j + 1 || j == last_j - 1)
                && eats(i, j, color) && !boardArray[i][j].equals("");
    }

    public boolean checkRook(int last_i, int last_j, int i, int j, int color) {
        if (((i == last_i) || (j == last_j)) && eats(i, j, color)) {
            if (i == last_i) {
                if (j > last_j) {
                    for (int k = 1; k < j - last_j; k++) {
                        if (eatsEverything(i, last_j + k)) {
                            return false;
                        }
                    }
                } else {
                    for (int k = 1; k < last_j - j; k++) {
                        if (eatsEverything(i, j + k)) {
                            return false;
                        }
                    }
                }
            } else {
                if (i > last_i) {
                    for (int k = 1; k < i - last_i; k++) {
                        if (eatsEverything(last_i + k, j)) {
                            return false;
                        }
                    }
                } else {
                    for (int k = 1; k < last_i - i; k++) {
                        if (eatsEverything(i + k, j)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean checkQueen(int last_i, int last_j, int i, int j, int color) {
        if (((abs(i - last_i) == abs(j - last_j)) || ((i == last_i) || (j == last_j))) && eats(i, j, color)) {
            if (i == last_i) {
                if (j > last_j) {
                    for (int k = 1; k < j - last_j; k++) {
                        if (eatsEverything(i, last_j + k)) {
                            return false;
                        }
                    }
                } else {
                    for (int k = 1; k < last_j - j; k++) {
                        if (eatsEverything(i, j + k)) {
                            return false;
                        }
                    }
                }
            }
            if (j == last_j) {
                if (i > last_i) {
                    for (int k = 1; k < i - last_i; k++) {
                        if (eatsEverything(last_i + k, j)) {
                            return false;
                        }
                    }
                } else {
                    for (int k = 1; k < last_i - i; k++) {
                        if (eatsEverything(i + k, j)) {
                            return false;
                        }
                    }
                }
            }
            if (i > last_i) {
                if (j > last_j) {
                    for (int k = 1; k < j - last_j; k++) {
                        if (eatsEverything(last_i + k, last_j + k)) {
                            return false;
                        }
                    }
                } else {
                    for (int k = 1; k < last_j - j; k++) {
                        if (eatsEverything(last_i + k, last_j - k)) {
                            return false;
                        }
                    }
                }
            } else {
                if (j > last_j) {
                    for (int k = 1; k < j - last_j; k++) {
                        if (eatsEverything(last_i - k, last_j + k)) {
                            return false;
                        }
                    }
                } else {
                    for (int k = 1; k < last_j - j; k++) {
                        if (eatsEverything(last_i - k, last_j - k)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void checkable(int l, int k){
        if (boardArray[l][k].toLowerCase().equals("k")){
            if (color == 1){
                System.out.println("check from white");
                whiteUnderCheck = true;}
            else{
                System.out.println("check from black");
                blackUnderCheck = true;}
        }
    }

    public ArrayList<Move> correctMoves() {
        ArrayList<Move> movesArray = new ArrayList<Move>();
        for (int m = 0; m < 8; m++)
            for (int n = 0; n < 8; n++) {
                if (boardArray[m][n].length() == 0)
                    continue;
                char s = boardArray[m][n].charAt(0);
                if (Character.isLowerCase(s) && color < 0 ||
                        Character.isUpperCase(s) && color > 0) {
                    String sl = boardArray[m][n].toLowerCase();
                    for (int k = 0; k < 8; k++)
                        for (int l = 0; l < 8; l++) {
                            if (sl.equals("p") && checkPawn(m, n, l, k, color)){
                                if (n != k)
                                    checkable(l, k);
                                movesArray.add(new Move(m, n, l, k));}
                            if (sl.equals("b") && checkBishop(m, n, l, k, color)){
                                checkable(l, k);
                                movesArray.add(new Move(m, n, l, k));}
                            if (sl.equals("n") && checkKnight(m, n, l, k, color)){
                                checkable(l, k);
                                movesArray.add(new Move(m, n, l, k));}
                            if (sl.equals("r") && checkRook(m, n, l, k, color)){
                                checkable(l, k);
                                movesArray.add(new Move(m, n, l, k));}
                            if (sl.equals("q") && checkQueen(m, n, l, k, color)){
                                checkable(l, k);
                                movesArray.add(new Move(m, n, l, k));}
                            if (sl.equals("k") && checkKing(m, n, l, k, color)){
                                checkable(l, k);
                                movesArray.add(new Move(m, n, l, k));}
                        }
                }
            }
        return movesArray;
    }
}

