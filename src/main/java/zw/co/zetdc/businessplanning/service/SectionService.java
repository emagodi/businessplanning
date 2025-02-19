package zw.co.zetdc.businessplanning.service;

import zw.co.zetdc.businessplanning.entities.Section;
import zw.co.zetdc.businessplanning.payload.request.SectionRequest;

import java.util.List;

public interface SectionService {

    Section createSection(SectionRequest sectionRequest);

    Section getSectionById(Long id);

    List<Section> getAllSections();

    Section updateSection(Long id, SectionRequest sectionRequest);

    void deleteSection(Long id);

}
