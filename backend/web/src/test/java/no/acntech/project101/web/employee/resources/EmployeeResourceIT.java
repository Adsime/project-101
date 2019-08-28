package no.acntech.project101.web.employee.resources;

import no.acntech.project101.Project101Application;
import no.acntech.project101.company.Company;
import no.acntech.project101.company.service.CompanyService;
import no.acntech.project101.employee.Employee;
import no.acntech.project101.employee.service.EmployeeService;
import no.acntech.project101.web.TestUtil;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Project101Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeResourceIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompanyService companyService;


    @Test
    void findAll() {
        final Company c = new Company("Accenture", "123412341");

        final Employee uno = new Employee("Uno", "Gonzales", LocalDate.of(1993, 12, 12));
        uno.setCompany(c);
        final Employee dos = new Employee("Dos", "Gonzales", LocalDate.of(1993, 12, 12));
        dos.setCompany(c);

        companyService.save(c);

        employeeService.save(uno);
        employeeService.save(dos);

        ResponseEntity<EmployeeDto[]> response = testRestTemplate.exchange(
          TestUtil.createURL(port, "/employees"),
          HttpMethod.GET,
          new HttpEntity<>(null, new HttpHeaders()),
          EmployeeDto[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<EmployeeDto> employees = Arrays.asList(response.getBody());
        assertThat(employees).isNotEmpty()
                .extracting(EmployeeDto::getFirstName, EmployeeDto::getLastName, EmployeeDto::getDateOfBirth)
                .contains(
                        Tuple.tuple(uno.getFirstName(), uno.getLastName(), uno.getDateOfBirth()),
                        Tuple.tuple(uno.getFirstName(), uno.getLastName(), uno.getDateOfBirth())
                );

    }

    @Test
    void findById() {
        final Company acme = companyService.save(new Company("ACME", "123456789"));

        final Employee ken = new Employee("Ken", "Guru", LocalDate.of(1994, 10, 1));
        ken.setCompany(acme);
        final Employee savedKen = employeeService.save(ken);


        ResponseEntity<EmployeeDto> response = testRestTemplate.exchange(
                TestUtil.createURL(port, "/employees/" + savedKen.getId()),
                HttpMethod.GET,
                new HttpEntity<>(null, new HttpHeaders()),
                EmployeeDto.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        EmployeeDto employeeDto = response.getBody();


        assertThat(employeeDto.getDateOfBirth()).isEqualTo(ken.getDateOfBirth());
        assertThat(employeeDto.getFirstName()).isEqualTo(ken.getFirstName());
        assertThat(employeeDto.getLastName()).isEqualTo(ken.getLastName());
    }

    @Test
    void createEmployee() {
        final Employee employee = new Employee("Adrian", "Melsom", LocalDate.of(1993, 7, 13));

        HttpEntity<Employee> entity = new HttpEntity<>(employee, new HttpHeaders());

        ResponseEntity response = testRestTemplate.exchange(
                TestUtil.createURL(port, "/employees"),
                HttpMethod.GET,
                entity,
                ResponseEntity.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().get(HttpHeaders.LOCATION).get(0)).containsPattern("\\/employees\\/d+");


    }

    @Test
    void deleteEmployee() {
        final Company acme = companyService.save(new Company("ACME", "123456789"));
        final Employee ken = new Employee("Ken", "Guru", LocalDate.of(1994, 10, 1));
        ken.setCompany(acme);
        final Employee savedKen = employeeService.save(ken);

        ResponseEntity response = testRestTemplate.exchange(
                TestUtil.createURL(port, "/employees/" + savedKen.getId()),
                HttpMethod.DELETE,
                new HttpEntity<>(null, new HttpHeaders()),
                ResponseEntity.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }
}
