package no.acntech.project101.employee.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import no.acntech.project101.employee.Employee;
import no.acntech.project101.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;

@Service
public class EmployeeService {

    private EmployeeRepository empRepo;

    public EmployeeService(final EmployeeRepository empRepo) {
        this.empRepo = empRepo;
    }

    public Employee save(final Employee employee) {
        return this.empRepo.save(employee);
    }

    public Optional<Employee> findById(final Long id) {
        return this.empRepo.findById(id);
    }

    public List<Employee> findAll() {
        return this.empRepo.findAll();
    }

    public void delete(final Long id) {
        if(empRepo.existsById(id))
            this.empRepo.deleteById(id);
    }

    public boolean update(final Employee employee) {
        boolean success = false;

        Optional existing = this.findById(employee.getId());
        if(existing.isPresent()) {
            empRepo.save(employee);
            success = true;
        }
        return success;
    }
}
