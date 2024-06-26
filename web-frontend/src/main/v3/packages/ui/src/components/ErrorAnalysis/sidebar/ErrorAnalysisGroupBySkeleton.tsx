import React from 'react';
import ContentLoader from 'react-content-loader';

export interface ErorrAnalysisGroupBySkeletonProps {}

export const ErorrAnalysisGroupBySkeleton = (props: ErorrAnalysisGroupBySkeletonProps) => {
  const viewBoxWidth = 300;
  const viewBoxHeight = 240;
  const titleBoxWidth = 80;
  const titleBoxHeight = 25;
  const titleBoxGap = 15;
  const rowBoxWidth = 160;
  const rowBoxHeight = 20;
  const xOffset = 20;
  const yOffset = 30;
  const rowCount = 5;
  const rowGap = 7;

  return (
    <ContentLoader
      speed={2}
      className="h-40"
      viewBox={`0 0 ${viewBoxWidth} ${viewBoxHeight}`}
      {...props}
    >
      <rect x={xOffset} y={yOffset} rx="4" ry="4" width={titleBoxWidth} height={titleBoxHeight} />
      {[...Array(rowCount)].map((_, i) => {
        return (
          <React.Fragment key={i}>
            <rect
              x={xOffset}
              y={yOffset + titleBoxHeight + titleBoxGap + (rowBoxHeight + rowGap) * i}
              width={rowBoxHeight}
              height={rowBoxHeight}
              rx="4"
              ry="4"
            />
            <rect
              x={xOffset * 2 + 10}
              y={yOffset + titleBoxHeight + titleBoxGap + (rowBoxHeight + rowGap) * i}
              rx="4"
              ry="4"
              width={rowBoxWidth}
              height={rowBoxHeight}
            />
          </React.Fragment>
        );
      })}
    </ContentLoader>
  );
};
