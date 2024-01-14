package sk.gov.knowledgegraph.model.refid.application;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import sk.gov.knowledgegraph.model.refid.application.component.ClassComponent;

@Data
@Accessors(chain = true)
public class URITemplate {

    /**
     * Šablóna cieľa dereferenciacie
     */
    private String redirectTemplateUrl;
    /**
     * Názov
     */
    @NotBlank
    private String name;
    /**
     * Indivíduum rozlišuje verzie?
     * Parameter {id}, ktorý reprezentuje identifikátor indivídua je vždy prítomný na konci referencovateľného identifikátora. Určité typy indivíduí môžu mať aj
     * dodatočné rozlíšenie cez verziu. Ak ano, tak sa súčasťou referencovateľného identifikátora hľadá v ceste aj {version} hneď za parametrom {id}, priČom
     * tento parameter je vždy zároveň aj posledný a za ním môžu byť už len QueryParam.
     * 
     */
    private boolean hasVersion;
    /**
     * Menný priestor referencovateľného identifikátora
     */
    @NotBlank
    private String uriNamespace;
    /**
     * Popis
     */
    private String description;
    /**
     * Platné od
     */
    @NotNull
    private LocalDate validFrom;
    /**
     * Platné do
     */
    private LocalDate validTo;
    /**
     * Ak je potrebné {id} parameter v URI identifikátora referencovať po častiach v redirectTemplateUrl. Napr. ak tvorca ID skladá z viacerých častí. Pri
     * referenčnom identifikátore rozhodnutia je ID = {messageId}_{skTalkClass}_{este_nieco} a oni to potrebujú špeciálne na dereferenčnom routery rozsekávať.
     * Samotný parameter je potrebné referencoať regulárnym výrazom do viacerých regexp groups, pričom následne v redirectTemplateUrl nereferencuju {id} ale
     * {id[0]}, {id[1]} podľa groupy identifikátora.
     * 
     */
    private String idRegexp;
    @Valid
    private List<URITemplateQueryParam> queryParams;
    private ClassComponent type;

}