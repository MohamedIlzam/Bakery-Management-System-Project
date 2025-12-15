package com.example.KodikaraGroupBusinessManagementApplication.services;


import com.example.KodikaraGroupBusinessManagementApplication.DTO.FairDeliveryReportDTO;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.FairDeliveryReportRepository;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.FairDeliveryRepository;
import com.example.KodikaraGroupBusinessManagementApplication.model.FairDelivery;
import com.example.KodikaraGroupBusinessManagementApplication.model.FairDeliveryItem;
import com.example.KodikaraGroupBusinessManagementApplication.model.FairDeliveryReport;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FairDeliveryReportService {
    private final FairDeliveryReportRepository reportRepository;
    @Autowired
    private final FairDeliveryRepository fairDeliveryRepository;
    @Transactional
    public FairDeliveryReportDTO generateDailyReport(LocalDate date){
        if(reportRepository.findByFreportDateAndReportType(date,"DAILY").isPresent()){
            throw new IllegalStateException("Daily Fair Delivery Report already exists for: "+date);
        }
        List<FairDelivery> deliveries = fairDeliveryRepository.findAll().stream()
                .filter(d ->{
                    boolean isDateMatch = d.getDeliveryDate().equals(date);
                    if(isDateMatch){
                        System.out.println("DEBUG: Found Delivery on " + date + " | ID: " + d.getDeliveryId() + " | Status: " + d.getStatus());
                    }
                    return isDateMatch && (
                            "RETURNED".equalsIgnoreCase(d.getStatus()) ||
                                    "RETURN".equalsIgnoreCase(d.getStatus()) ||
                                    "COMPLETED".equalsIgnoreCase(d.getStatus())
                    );
                })
                .collect(Collectors.toList());
        System.out.println("DEBUG: Total accepted deliveries for report: " + deliveries.size());
//                        d.getDeliveryDate().equals(date)&&"RETURNED".equalsIgnoreCase(d.getStatus()))
//                .collect(Collectors.toList());

        BigDecimal totalRevenue= BigDecimal.ZERO;
        BigDecimal totalProfit= BigDecimal.ZERO;
        BigDecimal totalExpences= BigDecimal.ZERO;

        for(FairDelivery fd:deliveries){
            BigDecimal deliveryRevenue=BigDecimal.ZERO;
            if(fd.getItems() !=null){
                for(FairDeliveryItem item:fd.getItems()){
                    int soldQty= item.getQtySent()-item.getQtyRemaining();
                    if(soldQty>0){
                        BigDecimal itemRevenue= item.getUnitPrice().multiply(BigDecimal.valueOf(soldQty));
                        deliveryRevenue=deliveryRevenue.add(itemRevenue);
                    }
                }
            }
            BigDecimal deisel=fd.getDieselAmount() !=null ?fd.getDieselAmount():BigDecimal.ZERO;
            BigDecimal extra=fd.getExtraPayments() != null ?fd.getExtraPayments():BigDecimal.ZERO;
            BigDecimal tax=fd.getTax() !=null ?fd.getTax():BigDecimal.ZERO;

            BigDecimal deliveryExpences=deisel.add(tax).add(extra);
            BigDecimal deliveryProfit=deliveryRevenue.subtract(deliveryExpences);

            totalRevenue=totalRevenue.add(deliveryRevenue);
            totalExpences=totalExpences.add(deliveryExpences);
            totalProfit=totalProfit.add(deliveryProfit);

        }
        FairDeliveryReport freport= new FairDeliveryReport();
        String generatedId = IdGenerator.generate("FDREP");
        System.out.println("DEBUG: Generated ID for Fair Report is: [" + generatedId + "] Length: " + generatedId.length());
        freport.setFreportDate(date);
        freport.setFreportId(IdGenerator.fairDailyReportId());
        freport.setReportMonth(date.format(DateTimeFormatter.ofPattern("yyyy-MM")));
        freport.setReportType("DAILY");
        freport.setTotalDeliveries(deliveries.size());
        freport.setTotalProfit(totalProfit);
        freport.setTotalExpenses(totalExpences);
        freport.setTotalRevenue(totalRevenue);
        freport.setGeneratedOn(LocalDateTime.now());

        return convertToDTO(reportRepository.save(freport));
    }
    public FairDeliveryReportDTO generateMonthlyReport(YearMonth yearmonth){
        String monthString =yearmonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        if(reportRepository.findByReportMonthAndReportType(monthString,"MONTHLY").isPresent()){
            throw new IllegalStateException("Monthly Fair Delivery Report already exists for: "+monthString);
        }
        List<FairDeliveryReport> dailyReports= reportRepository.findAllByreportMonthAndReportType(monthString,"DAILY");
        if(dailyReports.isEmpty() ){
            throw new IllegalStateException("No daily Fair Delivery Report found for: "+monthString);
        }

        BigDecimal mTotalRevenue= BigDecimal.ZERO;
        BigDecimal mTotalProfit= BigDecimal.ZERO;
        BigDecimal mTotalExpences= BigDecimal.ZERO;
        int totalDeliveries=0;

        for(FairDeliveryReport daily:dailyReports){
            mTotalRevenue=mTotalRevenue.add(daily.getTotalRevenue());
            mTotalExpences=mTotalExpences.add(daily.getTotalExpenses());
            mTotalProfit=mTotalProfit.add(daily.getTotalProfit());
            totalDeliveries += daily.getTotalDeliveries();
        }
        FairDeliveryReport FMreport=new FairDeliveryReport();
        FMreport.setFreportId(IdGenerator.generate("FMREP"));
        FMreport.setFreportDate(yearmonth.atDay(1));
        FMreport.setReportMonth(monthString);
        FMreport.setReportType("MONTHLY");
        FMreport.setTotalDeliveries(totalDeliveries);
        FMreport.setTotalRevenue(mTotalRevenue);
        FMreport.setTotalProfit(mTotalProfit);
        FMreport.setTotalExpenses(mTotalExpences);

        return convertToDTO(reportRepository.save(FMreport));
    }
    @Transactional(readOnly = true)
    public List<FairDeliveryReportDTO> getAllReports(){
        return reportRepository.findAll().stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<FairDeliveryReportDTO> getAllReportsByDate(LocalDate date){
        return reportRepository.findByFreportDate(date).stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public void deleteReport(String id){
        if(!reportRepository.existsById(id)){
            throw new IllegalStateException("No Fair Delivery Report found for "+id);
        }
        reportRepository.deleteById(id);
    }

    public FairDeliveryReportDTO convertToDTO(FairDeliveryReport report){
        FairDeliveryReportDTO dto = new FairDeliveryReportDTO();
        dto.setFreportID(report.getFreportId());
        dto.setFreportDate(report.getFreportDate());
        dto.setReportMonth(report.getReportMonth());
        dto.setReportType(report.getReportType());
        dto.setTotalDeliveries(report.getTotalDeliveries());
        dto.setTotalProfit(report.getTotalProfit());
        dto.setTotalExpences(report.getTotalExpenses());
        dto.setTotalRevenue(report.getTotalRevenue());
        dto.setGeneratedOn(report.getGeneratedOn());
        return dto;
    }
}



