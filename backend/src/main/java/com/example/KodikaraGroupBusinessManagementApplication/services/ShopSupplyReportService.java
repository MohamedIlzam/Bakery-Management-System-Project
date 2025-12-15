package com.example.KodikaraGroupBusinessManagementApplication.services;

import com.example.KodikaraGroupBusinessManagementApplication.DTO.ShopSupplyReportDTO;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.ShopSupplyReportRepository;
import com.example.KodikaraGroupBusinessManagementApplication.Repo.ShopSupplyRepository;
import com.example.KodikaraGroupBusinessManagementApplication.model.ShopSupply;
import com.example.KodikaraGroupBusinessManagementApplication.model.ShopSupplyItem;
import com.example.KodikaraGroupBusinessManagementApplication.model.ShopSupplyReport;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopSupplyReportService {
    private final ShopSupplyReportRepository shopReportRepository;
    private final ShopSupplyRepository  shopSupplyRepository;

    public ShopSupplyReportDTO generateDailyReport(LocalDate date){
        if(shopReportRepository.findBySreportDateAndReportType(date,"DAILY").isPresent()){
            throw new IllegalStateException("Daily Shop Supply Report already exists for: "+date);
        }
        List<ShopSupply> supplies=shopSupplyRepository.findAll().stream()
                .filter(s->s.getSupplyDate().equals(date))
                .collect(Collectors.toList());
        BigDecimal totalAmount=BigDecimal.ZERO;
        Set<String> shopIDs=new HashSet<>();
        for(ShopSupply Supply:supplies){
            if(Supply.getItems() !=null){
                for(ShopSupplyItem item:Supply.getItems()){
                    BigDecimal unitTotal=item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQtySupplied()));
                    totalAmount=totalAmount.add(unitTotal);

                    if(item.getShop() !=null){
                        shopIDs.add(item.getShop().getShopId());
                    }

                }
            }
        }
        ShopSupplyReport sreport=new ShopSupplyReport();
        sreport.setSreportId(IdGenerator.generate("SDREP"));
        sreport.setSreportDate(date);
        sreport.setReportMonth(date.format(DateTimeFormatter.ofPattern("yyyy-MM")));
        sreport.setReportType("DAILY");
        sreport.setTotalAmount(totalAmount);
        sreport.setTotalShopsServed(shopIDs.size());
        sreport.setTotalSupplies(supplies.size());

        return convertToDTO(shopReportRepository.save(sreport));
    }
    public ShopSupplyReportDTO generateMonthlyReport(YearMonth yearmonth){
        String monthString=yearmonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        if(shopReportRepository.findByReportMonthAndReportType(monthString,"MONTHLY").isPresent()){
            throw new IllegalStateException("Monthly Shop Supply Report already exists for: "+monthString);
        }
        List<ShopSupplyReport> dailyReports=shopReportRepository.findAllByReportMonthAndReportType(monthString,"DAILY");
        if(dailyReports.isEmpty()){
            throw new IllegalStateException("Monthly Shop Supply Report not found for: "+monthString);
        }
        BigDecimal mtotalAmount=BigDecimal.ZERO;
        int  totalShopsServed=0;
        int totalSupplies=0;
        for(ShopSupplyReport dailyReport:dailyReports){
            mtotalAmount=mtotalAmount.add(dailyReport.getTotalAmount());
            totalSupplies+=dailyReport.getTotalSupplies();
            totalShopsServed+=dailyReport.getTotalShopsServed();
        }
        ShopSupplyReport SMreport=new ShopSupplyReport();
        SMreport.setSreportId(IdGenerator.generate("SMREP"));
        SMreport.setSreportDate(yearmonth.atDay(1));
        SMreport.setReportMonth(monthString);
        SMreport.setReportType("MONTHLY");
        SMreport.setTotalAmount(mtotalAmount);
        SMreport.setTotalShopsServed(totalShopsServed);
        SMreport.setTotalSupplies(totalSupplies);

        return convertToDTO(shopReportRepository.save(SMreport));
    }
    @Transactional(readOnly = true)
    public List<ShopSupplyReportDTO> getAllReports(){
        return shopReportRepository.findAll().stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<ShopSupplyReportDTO> getAllReportsByDate(LocalDate date){
        return shopReportRepository.findBySreportDate(date).stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public void deleteReport(String Id){
        if(!shopReportRepository.existsById(Id)){
            throw new IllegalStateException("Shop Supply Report not found for id: "+Id);
        }
        shopReportRepository.deleteById(Id);
    }
    public ShopSupplyReportDTO convertToDTO(ShopSupplyReport shopReport){

        ShopSupplyReportDTO shopDTO=new ShopSupplyReportDTO();
        shopDTO.setSreportId(shopReport.getSreportId());
        shopDTO.setSreportDate(shopReport.getSreportDate());
        shopDTO.setReportMonth(shopReport.getReportMonth());
        shopDTO.setReportType(shopReport.getReportType());
        shopDTO.setTotalAmount(shopReport.getTotalAmount());
        shopDTO.setTotalShopsServed(shopReport.getTotalShopsServed());
        shopDTO.setTotalSupplies(shopReport.getTotalSupplies());
        shopDTO.setGeneratedOn(shopReport.getGeneratedOn());
        return shopDTO;
    }
}