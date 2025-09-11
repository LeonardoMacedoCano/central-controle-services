package br.com.lcano.usuario.dto;

import br.com.lcano.usuario.domain.Tema;
import lombok.Data;

@Data
public class TemaDTO {
    private Long id;
    private String title;
    private String primaryColor;
    private String secondaryColor;
    private String tertiaryColor;
    private String quaternaryColor;
    private String whiteColor;
    private String blackColor;
    private String grayColor;
    private String successColor;
    private String infoColor;
    private String warningColor;

    public TemaDTO fromEntity(Tema entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.primaryColor = entity.getPrimaryColor();
        this.secondaryColor = entity.getSecondaryColor();
        this.tertiaryColor = entity.getTertiaryColor();
        this.quaternaryColor = entity.getQuaternaryColor();
        this.whiteColor = entity.getWhiteColor();
        this.blackColor = entity.getBlackColor();
        this.grayColor = entity.getGrayColor();
        this.successColor = entity.getSuccessColor();
        this.infoColor = entity.getInfoColor();
        this.warningColor = entity.getWarningColor();

        return this;
    }

    public Tema toEntity() {
        Tema entity = new Tema();

        entity.setId(this.id);
        entity.setTitle(this.title);
        entity.setPrimaryColor(this.primaryColor);
        entity.setSecondaryColor(this.secondaryColor);
        entity.setTertiaryColor(this.tertiaryColor);
        entity.setQuaternaryColor(this.quaternaryColor);
        entity.setWhiteColor(this.whiteColor);
        entity.setBlackColor(this.blackColor);
        entity.setGrayColor(this.grayColor);
        entity.setSuccessColor(this.successColor);
        entity.setInfoColor(this.infoColor);
        entity.setWarningColor(this.warningColor);

        return entity;
    }
}