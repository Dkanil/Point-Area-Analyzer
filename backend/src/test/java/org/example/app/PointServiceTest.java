package org.example.app;

import org.example.app.dto.PointRequest;
import org.example.app.dto.PointResponse;
import org.example.app.repository.PointRepository;
import org.example.app.service.PointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
    @Mock
    private PointRepository pointRepository;
    @InjectMocks
    private PointService pointService;

    @Test
    public void hitFirstQuarter() {
        PointResponse pointResponse = pointService.processAndSavePoint(
                new PointRequest(1, 1, 3, null), "username");
        assertTrue(pointResponse.getIsHit());
    }

    @Test
    public void hitSecondQuarter() {
        PointResponse pointResponse = pointService.processAndSavePoint(
                new PointRequest(-0.5, 0.5, 3, null), "username");
        assertTrue(pointResponse.getIsHit());
    }

    @Test
    public void hitThirdQuarter() {
        PointResponse pointResponse = pointService.processAndSavePoint(
                new PointRequest(-2, -2, 4, null), "username");
        assertTrue(pointResponse.getIsHit());
    }

    @Test
    public void missFirstQuarter() {
        PointResponse pointResponse = pointService.processAndSavePoint(
                new PointRequest(2, 1.6, 3, null), "username");
        assertFalse(pointResponse.getIsHit());
    }

    @Test
    public void missSecondQuarter() {
        PointResponse pointResponse = pointService.processAndSavePoint(
                new PointRequest(-2, 2, 3, null), "username");
        assertFalse(pointResponse.getIsHit());
    }

    @Test
    public void missThirdQuarter() {
        PointResponse pointResponse = pointService.processAndSavePoint(
                new PointRequest(-4, -4, 1, null), "username");
        assertFalse(pointResponse.getIsHit());
    }

    @Test
    public void missFourthQuarter() {
        PointResponse pointResponse = pointService.processAndSavePoint(
                new PointRequest(0.001, -0.0001, 5, null), "username");
        assertFalse(pointResponse.getIsHit());
    }

    @Test
    public void hitZero() {
        PointResponse pointResponse = pointService.processAndSavePoint(
                new PointRequest(0, 0, 1, null), "username");
        assertTrue(pointResponse.getIsHit());
    }

    @Test
    public void hitFirstQuarterBorder() {
        PointResponse pointResponse = pointService.processAndSavePoint(
                new PointRequest(3, 1.5, 3, null), "username");
        assertTrue(pointResponse.getIsHit());
    }
}
