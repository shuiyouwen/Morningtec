package cn.com.morningtec.fontsizeselector;

/**
 * 文字实体类，保存刻度上方以及下方的显示文字，上方显示文字的属性，对应设置文字的大小
 * Created by Shui on 2018/4/21.
 */

public class Font {
    private Text topText;
    private Text bottomText;
    private int size;

    public Text getTopText() {
        return topText;
    }

    public void setTopText(Text topText) {
        this.topText = topText;
    }

    public Text getBottomText() {
        return bottomText;
    }

    public void setBottomText(Text bottomText) {
        this.bottomText = bottomText;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public class Text {
        private String text;
        private int color;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}
