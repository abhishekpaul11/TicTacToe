package com.example.tictactoe;

class Computer{

    private String Marker;

    public Computer() {
    }
    public void setMarker(String mark){
        Marker = mark;
    }
    public String getMarker(){
        return Marker;
    }

    public String takeTurn(String[][] board, String humanMarker) {

        // check if you can take a win horizontally
        for (int i = 0; i < 3; i++) {

            if (board[0][i].equals(board[1][i]) && board[0][i].equals(Marker)) {

                if (board[2][i] != humanMarker && board[2][i] != Marker) {
                    board[2][i] = Marker;
                    return "2 "+i;
                }

            }

        }

        for (int i = 0; i < 3; i++) {

            if (board[2][i].equals(board[1][i]) && board[2][i].equals(Marker)) {

                if (board[0][i] != humanMarker && board[0][i] != Marker) {
                    board[0][i] = Marker;
                    return "0 "+i;
                }
            }
        }

        for (int i = 0; i < 3; i++) {

            if (board[2][i].equals(board[0][i]) && board[2][i].equals(Marker)) {

                if (board[1][i] != humanMarker && board[1][i] != Marker) {
                    board[1][i] = Marker;
                    return "1 "+i;
                }
            }
        }


        // check if you can take a win vertically
        for (int i = 0; i < 3; i++) {

            if (board[i][0].equals(board[i][1]) && board[i][0].equals(Marker)) {

                if (board[i][2] != humanMarker && board[i][2] != Marker) {
                    board[i][2] = Marker;
                    return i+" 2";
                }

            }

        }

        for (int i = 0; i < 3; i++) {

            if (board[i][2].equals(board[i][1]) && board[i][2].equals(Marker)) {

                if (board[i][0] != humanMarker && board[i][0] != Marker) {
                    board[i][0] = Marker;
                    return i+" 0";
                }
            }
        }

        for (int i = 0; i < 3; i++) {

            if (board[i][2].equals(board[i][0]) && board[i][2].equals(Marker)) {

                if (board[i][1] != humanMarker && board[i][1] != Marker) {
                    board[i][1] = Marker;
                    return i+" 1";
                }
            }
        }


        // check if you can take a win diagonally bottom


        if (board[0][0].equals(board[1][1]) && board[0][0].equals(Marker)) {

            if (board[2][2] != humanMarker && board[2][2] != Marker) {
                board[2][2] = Marker;
                return "2 2";
            }
        }

        if (board[2][2].equals(board[1][1]) && board[2][2].equals(Marker)) {

            if (board[0][0] != humanMarker && board[0][0] != Marker) {
                board[0][0] = Marker;
                return "0 0";
            }
        }

        if (board[0][0].equals(board[2][2]) && board[0][0].equals(Marker)) {

            if (board[1][1] != humanMarker && board[1][1] != Marker) {
                board[1][1] = Marker;
                return "1 1";
            }
        }

        if (board[0][2].equals(board[1][1]) && board[0][2].equals(Marker)) {

            if (board[2][0] != humanMarker && board[2][0] != Marker) {
                board[2][0] = Marker;
                return "2 0";
            }
        }

        if (board[2][0].equals(board[1][1]) && board[2][0].equals(Marker)) {

            if (board[0][2] != humanMarker && board[0][2] != Marker) {
                board[0][2] = Marker;
                return "0 2";
            }
        }

        if (board[2][0].equals(board[0][2]) && board[2][0].equals(Marker)) {

            if (board[1][1] != humanMarker && board[1][1] != Marker) {
                board[1][1] = Marker;
                return "1 1";
            }
        }


        // BLOCKS!!!! //

        // check if you can block a win horizontally
        for (int i = 0; i < 3; i++) {

            if (board[0][i].equals(board[1][i]) && board[0][i].equals(humanMarker)) {
                if (board[2][i] != Marker && board[2][i] != humanMarker) {
                    board[2][i] = Marker;
                    return "2 "+i;
                }

            }

        }

        for (int i = 0; i < 3; i++) {

            if (board[2][i].equals(board[1][i]) && board[2][i].equals(humanMarker)) {

                if (board[0][i] != Marker && board[0][i] != humanMarker) {
                    board[0][i] = Marker;
                    return "0 "+i;
                }
            }
        }

        for (int i = 0; i < 3; i++) {

            if (board[2][i].equals(board[0][i]) && board[2][i].equals(humanMarker)) {

                if (board[1][i] != Marker && board[1][i] != humanMarker) {
                    board[1][i] = Marker;
                    return "1 "+i;
                }
            }
        }

        // check if you can block a win vertically


        for (int i = 0; i < 3; i++) {

            if (board[i][0].equals(board[i][1]) && board[i][0].equals(humanMarker)) {

                if (board[i][2] != Marker && board[i][2] != humanMarker) {
                    board[i][2] = Marker;
                    return i+" 2";
                }

            }

        }

        for (int i = 0; i < 3; i++) {

            if (board[i][2].equals(board[i][1]) && board[i][2].equals(humanMarker)) {

                if (board[i][0] != Marker && board[i][0] != humanMarker) {
                    board[i][0] = Marker;
                    return i+" 0";
                }

            }

        }

        for (int i = 0; i < 3; i++) {

            if (board[2][i].equals(board[0][i]) && board[2][i].equals(humanMarker)) {

                if (board[1][i] != Marker && board[1][i] != humanMarker) {
                    board[1][i] = Marker;
                    return "1 "+i;
                }

            }

        }


        // check if you can block a win diagonally


        if (board[0][0].equals(board[1][1]) && board[0][0].equals(humanMarker)) {

            if (board[2][2] != Marker && board[2][2] != humanMarker) {
                board[2][2] = Marker;
                return "2 2";
            }
        }

        if (board[2][2].equals(board[1][1]) && board[2][2].equals(humanMarker)) {

            if (board[0][0] != Marker && board[0][0] != humanMarker) {
                board[0][0] = Marker;
                return "0 0";
            }
        }

        if (board[0][0].equals(board[2][2]) && board[0][0].equals(humanMarker)) {
            if (board[1][1] != Marker && board[1][1] != humanMarker) {
                board[1][1] = Marker;
                return "1 1";
            }
        }

        if (board[0][2].equals(board[1][1]) && board[0][2].equals(humanMarker)) {

            if (board[2][0] != Marker && board[2][0] != humanMarker) {
                board[2][0] = Marker;
                return "2 0";
            }
        }

        if (board[2][0].equals(board[1][1]) && board[2][0].equals(humanMarker)) {

            if (board[0][2] != Marker && board[0][2] != humanMarker) {
                board[0][2] = Marker;
                return "0 2";
            }
        }

        if (board[0][2].equals(board[2][0]) && board[0][2].equals(humanMarker)) {
            if (board[1][1] != Marker && board[1][1] != humanMarker) {
                board[1][1] = Marker;
                return "1 1";
            }
        }

        // make random move if above rules dont apply
        int rand1 = 0;
        int rand2 = 0;

        while (true) {

            rand1 = (int) (Math.random() * 2);
            rand2 = (int) (Math.random() * 2);

            if (board[rand1][rand2] != "X" && board[rand1][rand2] != "O") {
                board[rand1][rand2] = Marker;
                return rand1+" "+rand2;
            }

        }
    }
}
