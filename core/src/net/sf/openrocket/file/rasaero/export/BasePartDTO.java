package net.sf.openrocket.file.rasaero.export;

import net.sf.openrocket.file.rasaero.CustomDoubleAdapter;
import net.sf.openrocket.file.rasaero.RASAeroCommonConstants;
import net.sf.openrocket.rocketcomponent.AxialStage;
import net.sf.openrocket.rocketcomponent.BodyTube;
import net.sf.openrocket.rocketcomponent.NoseCone;
import net.sf.openrocket.rocketcomponent.RocketComponent;
import net.sf.openrocket.rocketcomponent.Transition;
import net.sf.openrocket.rocketcomponent.position.AxialMethod;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.openrocket.file.rasaero.export.RASAeroSaver.RASAeroExportException;
import net.sf.openrocket.util.MathUtil;

/**
 * The base class for most RASAero components.
 */
@XmlRootElement
@XmlType(name="RASAeroBasePartDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class BasePartDTO {
    @XmlElement(name = RASAeroCommonConstants.PART_TYPE)
    private String partType;
    @XmlElement(name = RASAeroCommonConstants.LENGTH)
    @XmlJavaTypeAdapter(CustomDoubleAdapter.class)
    private Double length;
    @XmlElement(name = RASAeroCommonConstants.DIAMETER)
    @XmlJavaTypeAdapter(CustomDoubleAdapter.class)
    private Double diameter;
    @XmlElement(name = RASAeroCommonConstants.LOCATION)
    @XmlJavaTypeAdapter(CustomDoubleAdapter.class)
    private Double location;
    @XmlElement(name = RASAeroCommonConstants.COLOR)
    private String color;

    @XmlTransient
    private final RocketComponent component;

    /**
     * We need a default no-args constructor.
     */
    public BasePartDTO() {
        this.component = null;
    }

    protected BasePartDTO(RocketComponent component) throws RASAeroExportException {
        this.component = component;

        if (component instanceof BodyTube) {
            setPartType(RASAeroCommonConstants.BODY_TUBE);
            setDiameter(((BodyTube) component).getOuterRadius() * 2 * RASAeroCommonConstants.OPENROCKET_TO_RASAERO_LENGTH);
        } else if (component instanceof NoseCone) {
            setPartType(RASAeroCommonConstants.NOSE_CONE);
            NoseCone noseCone = (NoseCone) component;
            if (noseCone.isFlipped()) {
                throw new RASAeroExportException("Nose cone may not be flipped");
            }
            setDiameter(((NoseCone) component).getAftRadius() * 2 * RASAeroCommonConstants.OPENROCKET_TO_RASAERO_LENGTH);
        } else if (component instanceof Transition) {
            setPartType(RASAeroCommonConstants.TRANSITION);
            // This is a bit strange: I would expect diameter to be the fore radius, since you also have a rearDiamter
            // field for transitions, but okay
            setDiameter(((Transition) component).getAftRadius() * 2 * RASAeroCommonConstants.OPENROCKET_TO_RASAERO_LENGTH);
        } else if (component instanceof AxialStage) {
            setPartType(RASAeroCommonConstants.BOOSTER);
            AxialStage stage = (AxialStage) component;
            if (stage.getChildCount() == 0 || !(stage.getChild(0) instanceof BodyTube)) {
                throw new RASAeroExportException("First component of booster must be body tube");
            }
            setDiameter(stage.getBoundingRadius() * 2 * RASAeroCommonConstants.OPENROCKET_TO_RASAERO_LENGTH);
        } else {
            throw new RASAeroExportException("Unsupported component: " + component.getComponentName());
        }

        setLength(component.getLength() * RASAeroCommonConstants.OPENROCKET_TO_RASAERO_LENGTH);
        setLocation(component.getAxialOffset(AxialMethod.ABSOLUTE) * RASAeroCommonConstants.OPENROCKET_TO_RASAERO_LENGTH);
        setColor(RASAeroCommonConstants.OPENROCKET_TO_RASAERO_COLOR(component.getColor()));
    }

    public String getPartType() {
        return partType;
    }

    public void setPartType(String partType) {
        this.partType = partType;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) throws RASAeroExportException {
        if (MathUtil.equals(length, 0)) {
            throw new RASAeroExportException(String.format("Length of '%s' must be greater than 0", component.getName()));
        }
        this.length = length;
    }

    public Double getDiameter() {
        return diameter;
    }

    public void setDiameter(Double diameter) throws RASAeroExportException {
        if (MathUtil.equals(diameter, 0)) {
            throw new RASAeroExportException(String.format("Diameter of '%s' must be greater than 0", component.getName()));
        }
        this.diameter = diameter;
    }

    public Double getLocation() {
        return location;
    }

    public void setLocation(Double location) {
        this.location = location;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
