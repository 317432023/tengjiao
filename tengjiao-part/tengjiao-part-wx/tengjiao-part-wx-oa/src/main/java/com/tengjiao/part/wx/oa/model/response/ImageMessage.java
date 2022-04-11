package com.tengjiao.part.wx.oa.model.response;

/**
 * 图片消息
 */
public class ImageMessage extends BaseMessage {

    private Image image;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    public void setImage(String imageMmediaId) {
        this.image = new Image(imageMmediaId);
    }
}