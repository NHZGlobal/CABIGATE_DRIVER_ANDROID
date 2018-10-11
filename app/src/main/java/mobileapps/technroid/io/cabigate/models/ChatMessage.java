package mobileapps.technroid.io.cabigate.models;

import java.io.Serializable;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessage implements Serializable {
    private boolean isImage, isMine;
    private String content,name,senderid,created;

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public ChatMessage(boolean isImage, boolean isMine, String name, String content, String senderid, String created) {
        this.isImage = isImage;
        this.isMine = isMine;
        this.name = name;
        this.content = content;
        this.senderid = senderid;
        this.created = created;
    }



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }


}
