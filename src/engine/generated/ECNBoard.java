//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.05.12 at 02:08:48 PM IDT 
//


package engine.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}ECN-Layout"/>
 *       &lt;/sequence>
 *       &lt;attribute name="cards-count" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="black-cards-count" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "ecnLayout"
})
@XmlRootElement(name = "ECN-Board")
public class ECNBoard {

    @XmlElement(name = "ECN-Layout", required = true)
    protected ECNLayout ecnLayout;
    @XmlAttribute(name = "cards-count", required = true)
    protected int cardsCount;
    @XmlAttribute(name = "black-cards-count", required = true)
    protected int blackCardsCount;

    /**
     * Gets the value of the ecnLayout property.
     * 
     * @return
     *     possible object is
     *     {@link ECNLayout }
     *     
     */
    public ECNLayout getECNLayout() {
        return ecnLayout;
    }

    /**
     * Sets the value of the ecnLayout property.
     * 
     * @param value
     *     allowed object is
     *     {@link ECNLayout }
     *     
     */
    public void setECNLayout(ECNLayout value) {
        this.ecnLayout = value;
    }

    /**
     * Gets the value of the cardsCount property.
     * 
     */
    public int getCardsCount() {
        return cardsCount;
    }

    /**
     * Sets the value of the cardsCount property.
     * 
     */
    public void setCardsCount(int value) {
        this.cardsCount = value;
    }

    /**
     * Gets the value of the blackCardsCount property.
     * 
     */
    public int getBlackCardsCount() {
        return blackCardsCount;
    }

    /**
     * Sets the value of the blackCardsCount property.
     * 
     */
    public void setBlackCardsCount(int value) {
        this.blackCardsCount = value;
    }

}
