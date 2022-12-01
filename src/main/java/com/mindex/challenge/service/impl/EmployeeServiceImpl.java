package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getNumberOfReports(String id) {

        Employee employee = read(id);
        String employeeName = employee.getFirstName() + " " + employee.getLastName();

        return buildReportingStructure(employeeName, tallyNumberOfReports(employee) );
    }

    @Override
    public Compensation createEmployeeCompensation(Compensation employeeCompensation) {
        LOG.debug("Creating employee compensation[{}]", employeeCompensation);

        compensationRepository.insert(employeeCompensation);

        return employeeCompensation;
    }

    @Override
    public Compensation getEmployeeCompensation(String id) {
        Compensation employeeCompensation = compensationRepository.findByEmployeeId(id);

        if (employeeCompensation == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employeeCompensation;
    }


    private int tallyNumberOfReports(Employee employee)
    {
        int totalReports = 0;

        if(employee.getDirectReports() !=null) {

            totalReports = employee.getDirectReports().size();
            for (Employee e: employee.getDirectReports()) {
                Employee fullEmployee = read(e.getEmployeeId());
                totalReports += tallyNumberOfReports(fullEmployee);
            }
        }
        return totalReports;
    }


    private ReportingStructure buildReportingStructure(String employeeName, int totalNumberOfReports){
        ReportingStructure structure = new ReportingStructure();
        structure.setEmployee(employeeName);
        structure.setNumberOfReports(totalNumberOfReports);

        return structure;
    }
}
