package com.solunes.endeapp.utils;

/**
 * Created by jhonlimaster on 19-03-17.
 */

public class SingleChoiceItem {
    private int code;
    private String title;
    private int obsInd;
    private boolean check;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getObsInd() {
        return obsInd;
    }

    public void setObsInd(int obsInd) {
        this.obsInd = obsInd;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
