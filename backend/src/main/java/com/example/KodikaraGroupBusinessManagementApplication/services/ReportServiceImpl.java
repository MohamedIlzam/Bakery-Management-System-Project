package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.Repo.DailyReportRepository;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.MonthlyReportRepository;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.SaleDetailRepository;
import com.example.KodikaraGroupBusinessManagementApplication.DTO.*;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.*;
import com.example.KodikaraGroupBusinessManagementApplication.model.*;
import com.example.KodikaraGroupBusinessManagementApplication.exception.ResourceNotFoundException;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final DailyReportRepository dailyReportRepository;
    private final MonthlyReportRepository monthlyReportRepository;
    private final SaleRepository saleRepository;
    private final SaleService saleService;

    @Override
    @Transactional
    public DailyReportDTO generateDaily(LocalDate date){
        if(dailyReportRepository.existsByDreportDate(date)){
            throw new IllegalStateException("Daily Report already exists for: "+date);
        }
        List<Sale> saleForDate = saleRepository.findBySaleDate(date);
        BigDecimal totalSalesAmount = saleForDate.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int numberOfTransactions = saleForDate.size();
         DailyReport newReport = new DailyReport();
         newReport.setDreportId(IdGenerator.generate("DREP"));
         newReport.setDreportDate(date);
         newReport.setDtotalSales(totalSalesAmount);
         newReport.setDtotalTransac(numberOfTransactions);

         DailyReport savedReport =dailyReportRepository.save(newReport);
         return convertToDailyDTO(savedReport);
    }
    @Override
    public List<DailyReportDTO> getReportByDate(LocalDate date){
        List<DailyReport> dailyReports = dailyReportRepository.findByDreportDate(date);
        return dailyReports.stream().map(this::convertToDailyDTO).collect(Collectors.toList());
    }
    @Override
    public DailyReportDTO getDailyReportById(String dreportId){
        DailyReport dailyReport = dailyReportRepository.findById(dreportId)
                .orElseThrow(() -> new ResourceNotFoundException("Daily Report not found for: "+dreportId));
        return convertToDailyDTO(dailyReport);
    }
    @Override
    public void deleteByDate(String dreportId){
        if(!dailyReportRepository.existsById(dreportId)){
            throw new ResourceNotFoundException("Daily Report not found for: "+dreportId);
        }
        dailyReportRepository.deleteById(dreportId);
    }
    @Override
    @Transactional
    public MonthlyReportDTO generateMonthly(YearMonth yearMonth){
        String monthString = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        if(monthlyReportRepository.existsByMreportDate(monthString)){
            throw new IllegalStateException("Monthly Report already exists for: "+yearMonth);
        }
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Sale> salesForMonth = saleRepository.findBySaleDateBetween(startDate,endDate);
        BigDecimal totalSalesAmount =salesForMonth.stream().map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int numberTransactions = salesForMonth.size();
        MonthlyReport newReport= new MonthlyReport();
        newReport.setMreportId(IdGenerator.generate("MREP"));
        newReport.setMreportDate(monthString);
        newReport.setMtotalSales(totalSalesAmount);
        newReport.setMtotalTransac(numberTransactions);
        MonthlyReport savedReport =monthlyReportRepository.save(newReport);
        return convertToMonthlyDTO(savedReport);
    }
    @Override
    public List<MonthlyReportDTO> getMonthlyReportByMonth(YearMonth yearMonth){
        String monthString = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<MonthlyReport> monthlyReport = monthlyReportRepository.findByMreportDate(monthString);
        return monthlyReport.stream().map(this::convertToMonthlyDTO).collect(Collectors.toList());
    }
    @Override
    public MonthlyReportDTO getMonthlyReportById(String mreportId){
        MonthlyReport monthlyReport = monthlyReportRepository.findById(mreportId)
                .orElseThrow(() -> new ResourceNotFoundException("Monthly Report not found for: " + mreportId));
        return convertToMonthlyDTO(monthlyReport);
    }
    @Override
    public void deleteMonthlyReportById(String  mreportId){
        if(!monthlyReportRepository.existsById(mreportId)){
            throw new ResourceNotFoundException("Monthly Report not found for: "+mreportId);
        }
        monthlyReportRepository.deleteById(mreportId);
    }
    @Override
    public List<SaleResponseDTO> getFilterSaleData(LocalDate startDate,LocalDate endDate,
                                                   Optional<String> vehicleNo,
                                                   Optional<String> shopName,
                                                   Optional<String> driverName){
        List<Sale> saleList = saleRepository.findBySaleDateBetween(startDate,endDate);
        List<Sale> filterSale= saleList.stream()
                .filter(sale -> vehicleNo.isEmpty() || (sale.getVehicle() != null &&
                        sale.getVehicle().getVehicleNo().equalsIgnoreCase(vehicleNo.get())))
                .filter(sale -> shopName.isEmpty() || (sale.getShop() != null &&
                        sale.getShop().getShopName().equalsIgnoreCase(shopName.get())))
                .filter(sale -> driverName.isEmpty() || (sale.getDriver() != null &&
                        sale.getDriver().getName().equalsIgnoreCase(driverName.get())))
                .collect(Collectors.toList());

        return saleService.convertToResponseDTOList(filterSale);
    }
    private DailyReportDTO convertToDailyDTO(DailyReport dailyReport){
        return new DailyReportDTO(
                dailyReport.getDreportId(),
                dailyReport.getDreportDate(),
                dailyReport.getDtotalSales(),
                dailyReport.getDtotalTransac(),
                dailyReport.getGeneratedOn()
        );
    }
    private MonthlyReportDTO convertToMonthlyDTO(MonthlyReport monthlyReport){
        return new MonthlyReportDTO(
                monthlyReport.getMreportId(),
                monthlyReport.getMreportDate(),
                monthlyReport.getMtotalSales(),
                monthlyReport.getMtotalTransac(),
                monthlyReport.getGeneratedOn()
        );
    }


}