package no.acntech.project101.employee.repository;

import no.acntech.project101.company.config.CompanyDatabaseConfig;
import no.acntech.project101.employee.Employee;
import no.acntech.project101.employee.config.EmployeeDatabaseConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({CompanyDatabaseConfig.class, EmployeeDatabaseConfig.class})
@ContextConfiguration(classes = EmployeeRepository.class)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void save() {
        final Employee employee = new Employee("Adrian", "Melsom", LocalDate.of(1993, 7, 13));
        final Employee saved = employeeRepository.save(employee);

        assertThat(saved.getFirstName()).isEqualTo(employee.getFirstName());
        assertThat(saved.getLastName()).isEqualTo(employee.getLastName());
        assertThat(saved.getDateOfBirth()).isEqualTo(employee.getDateOfBirth());
    }
}