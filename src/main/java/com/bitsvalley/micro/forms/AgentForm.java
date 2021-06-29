package com.bitsvalley.micro.forms;

import com.bitsvalley.micro.domain.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AgentForm extends User {

    private int workingHrsPerWeek;
    private LocalTime startTime;
    private LocalTime closingTime;
    private List<DayOfWeek> workingDays;
    private LocalDate startDate = LocalDate.now();

    public LocalDate getStartDate() { return startDate; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public List<DayOfWeek> getWorkingDays() { return workingDays; }

    public void setWorkingDays(List<DayOfWeek> workingDays) { this.workingDays = workingDays; }

    public LocalTime getClosingTime() { return closingTime; }

    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }

    public int getWorkingHrsPerWeek() { return workingHrsPerWeek; }

    public void setWorkingHrsPerWeek(int workingHrsPerWeek) { this.workingHrsPerWeek = workingHrsPerWeek; }

    public LocalTime getStartTime() { return startTime; }

    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
}
