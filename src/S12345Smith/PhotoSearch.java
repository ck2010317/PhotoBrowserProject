package S12345Smith;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PhotoSearch {
    public static List<Photo> searchByTags(List<Photo> photos, List<String> tags, boolean andOperation) {
        if (andOperation) {
            return photos.stream()
                    .filter(photo -> tags.stream().allMatch(photo.getTags()::contains))
                    .collect(Collectors.toList());
        } else {
            return photos.stream()
                    .filter(photo -> tags.stream().anyMatch(photo.getTags()::contains))
                    .collect(Collectors.toList());
        }
    }

    public static List<Photo> searchByDescription(List<Photo> photos, String keyword) {
        Pattern pattern = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);
        return photos.stream()
                .filter(photo -> pattern.matcher(photo.getDescription()).find())
                .collect(Collectors.toList());
    }

    public static List<Photo> searchByDate(List<Photo> photos, Date startDate, Date endDate) {
        return photos.stream()
                .filter(photo -> !photo.getDate().before(startDate) && !photo.getDate().after(endDate))
                .collect(Collectors.toList());
    }
}
