package edu.MD.statThermodynamic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;
import edu.MD.utility.MDConstants;
import edu.MD.utility.YComparator;

public class MonatomicYAxialSmoothDensityCalculator implements IDensityCalculator {
	private int numParticles;
	private int totalNumOfParticles;
	private MDVector systemBoundary;

	public static IDensityCalculator getInstance(int totalNumberOfParticles, MDVector systemBoundary) {
		MonatomicYAxialSmoothDensityCalculator instance = new MonatomicYAxialSmoothDensityCalculator();
		instance.numParticles = 1;
		instance.totalNumOfParticles = totalNumberOfParticles;
		instance.systemBoundary = systemBoundary;
		return instance;
	}

	public static IDensityCalculator getInstance(int totalNumberOfParticles, MDVector systemBoundary,
			double molarVaporDensity, int nBins) {
		MonatomicYAxialSmoothDensityCalculator instance = new MonatomicYAxialSmoothDensityCalculator();

		MDNumber crossSectionArea = systemBoundary.getCartesianComponent()[0]
				.times(systemBoundary.getCartesianComponent()[2]);
		MDNumber axialLength = systemBoundary.getCartesianComponent()[1];
		MDNumber binLength = axialLength.divide(nBins);
		MDNumber binVolume = binLength.times(crossSectionArea);
		instance.numParticles = binVolume.times(molarVaporDensity * MDConstants.AVOGADRO).round();

		instance.totalNumOfParticles = totalNumberOfParticles;
		instance.systemBoundary = systemBoundary;
		return instance;

	}

	/**
	 * @param positions
	 *            Iterable<MDVector>
	 * @param systemBoundary
	 * @return DensityProfiles in List<List<MDNumber>> format. Two lists, the
	 *         1st list is the y location, the 2nd list is the corresponding
	 *         local density value.
	 */
	public List<List<MDNumber>> calculate(Iterable<MDVector> positions) {

		List<MDVector> listOfPositions;
		if (positions instanceof Collection<?>) {
			Collection<MDVector> collectionOfPositions = (Collection<MDVector>) positions;
			listOfPositions = new ArrayList<MDVector>(collectionOfPositions);
		} else {
			listOfPositions = new ArrayList<>(totalNumOfParticles);
			for (MDVector position : positions) {
				listOfPositions.add(position);
			}
		}

		listOfPositions.sort(new YComparator());

		List<List<MDNumber>> density = new ArrayList<>(2);
		List<MDNumber> yPositions = new ArrayList<>(totalNumOfParticles - numParticles + 1);
		List<MDNumber> localDensity = new ArrayList<>(totalNumOfParticles - numParticles + 1);
		density.add(yPositions);
		density.add(localDensity);

		MDNumber crossSectionArea = systemBoundary.getCartesianComponent()[0]
				.times(systemBoundary.getCartesianComponent()[2]);

		ArrayDeque<MDNumber> yOfParticles = new ArrayDeque<>();
		MDNumber yCurrent = listOfPositions.get(0).getCartesianComponent()[1];
		MDNumber ySum = yCurrent;
		yOfParticles.addLast(yCurrent);
		int idx = 1;

		while (idx < totalNumOfParticles) {
			yCurrent = listOfPositions.get(idx).getCartesianComponent()[1];
			yOfParticles.addLast(yCurrent);
			ySum = ySum.plus(yCurrent);
			if (yOfParticles.size() >= numParticles && !yOfParticles.peekLast().equals(yOfParticles.peekFirst())) {
				// calculate local density and add to the lists
				MDNumber yMean = ySum.divide(yOfParticles.size());
				yPositions.add(yMean);
				MDNumber localVolume = yOfParticles.peekLast().minus(yOfParticles.peekFirst()).times(crossSectionArea);
				MDNumber molarDensity = localVolume.pow(-1)
						.times(((double) yOfParticles.size()) / MDConstants.AVOGADRO);
				localDensity.add(molarDensity);
				// reset counter
				MDNumber yDiscard = yOfParticles.removeFirst();
				ySum = ySum.minus(yDiscard);
				while (yOfParticles.size() >= numParticles && yOfParticles.peekFirst().equals(yDiscard)) {
					yDiscard = yOfParticles.removeFirst();
					ySum = ySum.minus(yDiscard);
				}
			}
			idx++;
		}
		// handle the last local density if it is not handled
		if (yOfParticles.peekLast().equals(yOfParticles.peekFirst())) {
			// extend backward
			int sizeLastGroup = yOfParticles.size();
			int i = totalNumOfParticles - 1 - sizeLastGroup;
			MDNumber yAdded = listOfPositions.get(i).getCartesianComponent()[1];
			while (yAdded.equals(yCurrent)) {
				ySum = ySum.plus(yAdded);
				sizeLastGroup++;
				i--;
				yAdded = listOfPositions.get(i).getCartesianComponent()[1];
			}
			ySum = ySum.plus(yAdded);
			sizeLastGroup++;
			MDNumber yMean = ySum.divide(sizeLastGroup);
			yPositions.add(yMean);
			MDNumber localVolume = yCurrent.minus(yAdded).times(crossSectionArea);
			MDNumber molarDensity = localVolume.pow(-1).times(((double) sizeLastGroup) / MDConstants.AVOGADRO);
			localDensity.add(molarDensity);
		} else if (yOfParticles.size() < numParticles) {
			// just use the smaller group
			MDNumber yMean = ySum.divide(yOfParticles.size());
			yPositions.add(yMean);
			MDNumber localVolume = yOfParticles.peekLast().minus(yOfParticles.peekFirst()).times(crossSectionArea);
			MDNumber molarDensity = localVolume.pow(-1).times(((double) yOfParticles.size()) / MDConstants.AVOGADRO);
			localDensity.add(molarDensity);
		}

		return density;

	}
}
