package no.acntech.project101.employee.service;

import no.acntech.project101.employee.Employee;
import no.acntech.project101.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void save() {
        final Employee employee = new Employee("Ken", "Guru", LocalDate.of(1994, 10, 1));

        when(employeeRepository.save(employee)).thenReturn(employee);

        final Employee saved = employeeRepository.save(employee);

        assertThat(saved.getFirstName()).isEqualTo(employee.getFirstName());
        assertThat(saved.getLastName()).isEqualTo(employee.getLastName());
        assertThat(saved.getDateOfBirth()).isEqualTo(employee.getDateOfBirth());
    }

    @Test
    void findById() {
        final Employee employee = new Employee("Ken", "Guru", LocalDate.of(1994, 10, 1));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        final Employee foundEmployee = employeeService.findById(1L).get();

        assertThat(foundEmployee.getFirstName()).isEqualTo(employee.getFirstName());
        assertThat(foundEmployee.getLastName()).isEqualTo(employee.getLastName());
        assertThat(foundEmployee.getDateOfBirth()).isEqualTo(employee.getDateOfBirth());
    }

    @Test
    void findAll() {
        final Employee one = new Employee("Ken", "Guru", LocalDate.of(1994, 10, 1));
        final Employee two = new Employee("Kent", "Gurur", LocalDate.of(1994, 10, 1));

        when(employeeRepository.findAll()).thenReturn(Arrays.asList(one, two));

        List<Employee> l = employeeRepository.findAll();

        assertThat(l).hasSize(2);
        assertThat(l).contains(one, two);

    }

    @Test
    void deleteExisting() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        employeeService.delete(1L);

        verify(employeeRepository).existsById(1L);
    }

    @Test
    void deleteNonExisting() {
        when(employeeRepository.existsById(1L)).thenReturn(false);

        employeeService.delete(1L);

        verify(employeeRepository).existsById(1L);
        verifyNoMoreInteractions(employeeRepository);
    }
}