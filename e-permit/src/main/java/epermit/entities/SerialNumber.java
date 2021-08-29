package epermit.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import epermit.models.enums.SerialNumberState;
import epermit.models.enums.PermitType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "serial_numbers")
public class SerialNumber {
    @Id
    @GeneratedValue
    private int id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private SerialNumberState state;

    @Column(name = "authority_code", nullable = false)
    private String authorityCode;

    @Column(name = "serial_number", nullable = false)
    private int serialNumber;

    @Column(name = "permit_year", nullable = false)
    private int permitYear;

    @Column(name = "permit_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermitType permitType;
}
