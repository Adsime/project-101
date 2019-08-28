package no.acntech.project101.web.employee.resources;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import no.acntech.project101.employee.Employee;
import no.acntech.project101.employee.service.EmployeeService;
import no.acntech.project101.web.employee.resources.converter.EmployeeConverter;
import no.acntech.project101.web.employee.resources.converter.EmployeeDtoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("employees")
public class EmployeeResource {

    private ArrayList<EmployeeDto> employees;

    private EmployeeService empService;
    private EmployeeDtoConverter dtoConverter;
    private EmployeeConverter empConverter;

    public EmployeeResource(final EmployeeService empService, final EmployeeDtoConverter dtoConverter,
                            final EmployeeConverter empConverter) {

        this.empService = empService;
        this.dtoConverter = dtoConverter;
        this.empConverter = empConverter;

        this.employees = new ArrayList<>();

        IntStream.range(0, 100).forEach(i -> this.employees.add(
                new EmployeeDto((long) i, "Adrian", "Melsom", LocalDate.of(1993, 7, 13), 1l)
        ));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> findAll() {
        return ResponseEntity.ok(this.empService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<EmployeeDto> findById(@PathVariable final long id) {
        ResponseEntity response = ResponseEntity.notFound().build();
        Optional employee = this.empService.findById(id);
        if(employee.isPresent()) {
            response = ResponseEntity.ok(dtoConverter.convert((Employee) employee.get()));
        }
        return response;
    }

    @PostMapping
    public ResponseEntity createEmployee(@RequestBody final EmployeeDto employeeDto) {
        ResponseEntity response = new ResponseEntity(HttpStatus.BAD_REQUEST);

        Employee employee = this.empConverter.convert(employeeDto);
        Employee saved = this.empService.save(employee);

        if(saved != null) {
            final URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();
            response = ResponseEntity.created(uri).build();
        }

        return response;
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteEmployee(@PathVariable final long id) {
        this.empService.delete(id);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity updateEmployee(@PathVariable final long id, @RequestBody final EmployeeDto employeeDto) {

        ResponseEntity response = new ResponseEntity(HttpStatus.BAD_REQUEST);
        Employee employee = this.empConverter.convert(employeeDto);
        if(employee != null) {
            employee.setId(id);
        }

        if(this.empService.update(employee)) {
            response = ResponseEntity.ok("");
        }

        return response;
    }
}
